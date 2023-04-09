package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.exceptions.ExamSubmissionLockedException;
import app.stepanek.gamajun.exceptions.ExamSubmissionNotFoundException;
import app.stepanek.gamajun.exceptions.ResourceNotOwnedByCurrentUserException;
import app.stepanek.gamajun.graphql.ExamSubmissionCheckpointInput;
import app.stepanek.gamajun.graphql.ExamSubmissionGradeInput;
import app.stepanek.gamajun.graphql.ExamSubmissionSubmitInput;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class ExamSubmissionService {
    private final ExamSubmissionDao examSubmissionDao;
    @Lazy
    private final ValidatorService validatorService;
    private final IAuthenticationFacade authenticationFacade;


    @Autowired
    public ExamSubmissionService(ExamSubmissionDao examSubmissionDao, ValidatorService validatorService, IAuthenticationFacade authenticationFacade) {
        this.examSubmissionDao = examSubmissionDao;
        this.validatorService = validatorService;
        this.authenticationFacade = authenticationFacade;
    }

    //******
    //Create
    //******

    public ExamSubmission save(ExamSubmission sub) {
        log.info("User {} is saving exam submission with name id {}", authenticationFacade.getUsername(), sub.getId());
        return examSubmissionDao.save(sub);
    }

    //****
    //Read
    //****

    @Transactional
    public ExamSubmission findById(UUID id) {
        log.info("User {} is finding exam submission with id {}", authenticationFacade.getUsername(), id);

        var examSubmission = examSubmissionDao.findById(id)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(id)));

        if (!authenticationFacade.isResourceOwner(examSubmission.getUser())) {
            log.warn("User {} is not allowed to access exam submission with id {}", authenticationFacade.getUsername(), id);
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(id, authenticationFacade.getUsername()));
        }

        return examSubmission;
    }

    @Transactional
    public List<ExamSubmission> findAll() {
        log.info("User {} is finding all exam submissions", authenticationFacade.getUsername());
        return examSubmissionDao.findAll();
    }

    @Transactional
    public List<ExamSubmission> findAllByExam(UUID examId) {
        log.info("User {} is finding all exam submissions for exam with id {}", authenticationFacade.getUsername(), examId);
        return examSubmissionDao.findByExam_Id(examId);
    }

    @Transactional
    public List<ExamSubmission> mySubmissions() {
        log.info("User {} is finding all his exam submissions", authenticationFacade.getUsername());
        return examSubmissionDao.findByUser_Username(authenticationFacade.getUsername());
    }

    //******
    //Delete
    //******

    @Transactional
    public void delete(UUID id) {
        log.info("User {} is deleting exam submission with id {}", authenticationFacade.getUsername(), id);
        examSubmissionDao.deleteById(id);
    }

    @Transactional
    public void deleteByUser(User user) {
        log.info("User {} is deleting all exam submissions for user {}", authenticationFacade.getUsername(), user.getUsername());
        examSubmissionDao.deleteExamSubmissionsByUser(user);
    }

    //*******
    //Actions
    //*******

    @Transactional
    public ExamSubmission checkpointStudentExam(ExamSubmissionCheckpointInput examSubmissionCheckpointInput) {
        log.info("User {} is checkpointing exam submission with id {}", authenticationFacade.getUsername(), examSubmissionCheckpointInput.getId());

        var examSubmission = examSubmissionDao.findById(examSubmissionCheckpointInput.getId())
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionCheckpointInput.getId())));

        if (!authenticationFacade.isResourceOwner(examSubmission.getUser())) {
            log.warn("User {} is not allowed to access exam submission with id {}", authenticationFacade.getUsername(), examSubmissionCheckpointInput.getId());
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(examSubmissionCheckpointInput.getId(), authenticationFacade.getUsername()));
        }

        examSubmission.setXml(examSubmissionCheckpointInput.getXml());

        return examSubmissionDao.save(examSubmission);
    }

    @Transactional
    public ExamSubmission submitStudentExam(ExamSubmissionSubmitInput examSubmissionSubmitInput) {
        log.info("User {} is submitting exam submission with id {}", authenticationFacade.getUsername(), examSubmissionSubmitInput.getId());

        var examSubmission = examSubmissionDao.findById(examSubmissionSubmitInput.getId())
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionSubmitInput.getId())));

        if (!examSubmission.getExamSubmissionState().equals(ExamSubmissionState.Draft)) {
            log.warn("Exam submission with id {} submitted by user {} is not a draft anymore", examSubmissionSubmitInput.getId(), authenticationFacade.getUsername());
            throw new ExamSubmissionLockedException("Exam submission '%s' is not a draft anymore".formatted(examSubmissionSubmitInput.getId()));
        }

        if (!authenticationFacade.isResourceOwner(examSubmission.getUser())) {
            log.warn("User {} is not allowed to access exam submission with id {}", authenticationFacade.getUsername(), examSubmissionSubmitInput.getId());
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(examSubmissionSubmitInput.getId(), authenticationFacade.getUsername()));
        }

        examSubmission.setSubmittedAt(Instant.now());
        examSubmission.setXml(examSubmissionSubmitInput.getXml());
        examSubmission.setExamSubmissionState(ExamSubmissionState.Submitted);


        var validatorReport = validatorService.validateSubmission(examSubmission);
        examSubmission.setValidatorReport(validatorReport);

        examSubmission = examSubmissionDao.save(examSubmission);

        return examSubmission;
    }

    @Transactional
    public ExamSubmission gradeStudentExam(ExamSubmissionGradeInput examSubmissionGradeInput) {
        log.info("User {} is grading exam submission with id {}", authenticationFacade.getUsername(), examSubmissionGradeInput.getId());

        var examSubmission = examSubmissionDao.findById(examSubmissionGradeInput.getId())
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionGradeInput.getId())));

        if (examSubmission.getExamSubmissionState().equals(ExamSubmissionState.Draft))
            throw new ExamSubmissionLockedException("Exam submission '%s' cannot be graded yet".formatted(examSubmissionGradeInput.getId()));

        examSubmission.setPoints(examSubmissionGradeInput.getPoints());
        examSubmission.setComment(examSubmissionGradeInput.getComment());
        examSubmission.setExamSubmissionState(ExamSubmissionState.Graded);

        examSubmission = examSubmissionDao.save(examSubmission);

        return examSubmission;
    }
}
