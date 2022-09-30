package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import app.stepanek.gamajun.exceptions.ExamNotFoundException;
import app.stepanek.gamajun.exceptions.ExamSubmissionLockedException;
import app.stepanek.gamajun.graphql.CreateExamInput;
import app.stepanek.gamajun.graphql.UpdateExamInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.repository.ExamDao;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class ExamService {
    private final AssignmentDao assignmentDao;
    private final ExamDao examDao;
    private final ExamSubmissionDao examSubmissionDao;
    private final IAuthenticationFacade authenticationFacade;

    @Autowired
    public ExamService(AssignmentDao assignmentDao, ExamDao examDao, ExamSubmissionDao examSubmissionDao, IAuthenticationFacade authenticationFacade) {
        this.assignmentDao = assignmentDao;
        this.examDao = examDao;
        this.examSubmissionDao = examSubmissionDao;
        this.authenticationFacade = authenticationFacade;
    }

    public ExamSubmission beginExam(UUID examId) {
        var exam = examDao.findById(examId).orElseThrow(() -> new ExamNotFoundException("Exam with id '%s' was not found".formatted(examId)));

        var userExamSubmissions = examSubmissionDao.findByExam_Author(authenticationFacade.getUsername());
        if (userExamSubmissions.stream().map(ExamSubmission::getExam).anyMatch(e -> e.equals(exam)))
            throw new ExamSubmissionLockedException("You already attended exam with id '%s'".formatted(examId));

        Random rand = new Random();

        ExamSubmission examSubmission = new ExamSubmission();
        examSubmission.setAssignment(exam.getAssignments().get(rand.nextInt(exam.getAssignments().size())));
        examSubmission.setAuthor(authenticationFacade.getUsername());
        examSubmission.setStartedAt(Instant.now());
        examSubmission.setExam(exam);
        examSubmission.setExamSubmissionState(ExamSubmissionState.Draft);

        return examSubmissionDao.save(examSubmission);
    }

    public List<Exam> getOpenedExams() {
        var exams = examDao.findByAccessibleFromLessThanEqualAndAccessibleToGreaterThanEqual(Instant.now(), Instant.now());
        var submissions = examSubmissionDao.findByExam_Author(authenticationFacade.getUsername());

        var attendedExams = submissions.stream().map(ExamSubmission::getExam).toList();
        exams.removeAll(attendedExams);

        return exams;
    }

    @Transactional
    public Exam createExam(CreateExamInput createExamInput) {
        Exam exam = new Exam();

        exam.setTitle(createExamInput.getTitle());
        exam.setAuthor(authenticationFacade.getUsername());
        exam.setAccessibleFrom(createExamInput.getAccessibleFrom());
        exam.setAccessibleTo(createExamInput.getAccessibleTo());
        exam.setAssignments(assignmentDao.findAllById(createExamInput.getAssignmentIds()));

        return examDao.save(exam);
    }

    public List<Exam> findAll() {
        return examDao.findAll();
    }

    public Exam findById(UUID examId) {
        return examDao
                .findById(examId)
                .orElseThrow(() -> new ExamNotFoundException("Exam with id '%s' was not found".formatted(examId)));
    }

    public void deleteExam(UUID examId) {
        examDao.deleteById(examId);
    }

    public Exam update(UpdateExamInput updateExamInput) {
        Exam exam = findById(updateExamInput.getId());

        exam.setTitle(updateExamInput.getTitle());
        exam.setAccessibleFrom(updateExamInput.getAccessibleFrom());
        exam.setAccessibleTo(updateExamInput.getAccessibleTo());
        exam.setAssignments(assignmentDao.findAllById(updateExamInput.getAssignmentIds()));

        return examDao.save(exam);
    }
}
