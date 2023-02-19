package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import app.stepanek.gamajun.exceptions.ExamSubmissionLockedException;
import app.stepanek.gamajun.exceptions.ExamSubmissionNotFoundException;
import app.stepanek.gamajun.exceptions.ResourceNotOwnedByCurrentUserException;
import app.stepanek.gamajun.graphql.ExamSubmissionCheckpointInput;
import app.stepanek.gamajun.graphql.ExamSubmissionSubmitInput;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
public class ExamSubmissionService {
    private final ExamSubmissionDao examSubmissionDao;
    @Autowired
    @Lazy
    private ValidatorService validatorService;
    private final IAuthenticationFacade authenticationFacade;


    @Autowired
    public ExamSubmissionService(ExamSubmissionDao examSubmissionDao, IAuthenticationFacade authenticationFacade) {
        this.examSubmissionDao = examSubmissionDao;
        this.authenticationFacade = authenticationFacade;
    }

    //******
    //Create
    //******

    public ExamSubmission save(ExamSubmission sub) {
        return examSubmissionDao.save(sub);
    }

    //****
    //Read
    //****

    @Transactional
    public ExamSubmission findById(UUID id) {
        var examSubmission = examSubmissionDao.findById(id)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(id)));

        /*if (!authenticationFacade.getUsername().equals(examSubmission.getUser().getUsername()))
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(id, authenticationFacade.getUsername()));*/

        return examSubmission;
    }

    @Transactional
    public List<ExamSubmission> findAll() {
        return examSubmissionDao.findAll();
    }

    @Transactional
    public List<ExamSubmission> findAllByExam(UUID examId) {
        return examSubmissionDao.findByExam_Id(examId);
    }

    @Transactional
    public List<ExamSubmission> mySubmissions() {
        return examSubmissionDao.findByUser_Username(authenticationFacade.getUsername());
    }

    @Transactional
    public ExamSubmission getExamSubmission(UUID examSubmissionId) {
        var examSubmission = examSubmissionDao.findById(examSubmissionId)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionId)));

        /*if (!authenticationFacade.getUsername().equals(examSubmission.getUser().getUsername()))
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(examSubmissionId, authenticationFacade.getUsername()));*/

        return examSubmission;
    }

    //******
    //Delete
    //******

    @Transactional
    public boolean delete(UUID id) {
        examSubmissionDao.deleteById(id);
        return true;
    }

    //*******
    //Actions
    //*******

    @Transactional
    public ExamSubmission checkpointStudentExam(ExamSubmissionCheckpointInput examSubmissionCheckpointInput) {
        var examSubmission = examSubmissionDao.findById(examSubmissionCheckpointInput.getId())
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionCheckpointInput.getId())));

        /*if (!authenticationFacade.getUsername().equals(examSubmission.getUser().getUsername()))
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(examSubmissionCheckpointInput.getId(), authenticationFacade.getUsername()));*/

        examSubmission.setXml(examSubmissionCheckpointInput.getXml());

        return examSubmissionDao.save(examSubmission);
    }

    @Transactional
    public ExamSubmission submitStudentExam(ExamSubmissionSubmitInput examSubmissionSubmitInput) {
        var examSubmission = examSubmissionDao.findById(examSubmissionSubmitInput.getId())
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionSubmitInput.getId())));

        if (!examSubmission.getExamSubmissionState().equals(ExamSubmissionState.Draft))
            throw new ExamSubmissionLockedException("Exam submission '%s' is not a draft anymore".formatted(examSubmissionSubmitInput.getId()));

        /*if (!authenticationFacade.getUsername().equals(examSubmission.getUser().getUsername()))
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(examSubmissionSubmitInput.getId(), authenticationFacade.getUsername()));*/

        examSubmission.setSubmittedAt(Instant.now());
        examSubmission.setXml(examSubmissionSubmitInput.getXml());
        examSubmission.setExamSubmissionState(ExamSubmissionState.Submitted);


        var validatorReport = validatorService.validateSubmission(examSubmission);
        examSubmission.setValidatorReport(validatorReport);

        examSubmission = examSubmissionDao.save(examSubmission);

        return examSubmission;
    }
}
