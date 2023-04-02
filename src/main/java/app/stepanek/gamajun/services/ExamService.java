package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import app.stepanek.gamajun.exceptions.ClassroomNotFoundException;
import app.stepanek.gamajun.exceptions.ExamNotFoundException;
import app.stepanek.gamajun.exceptions.ExamSubmissionLockedException;
import app.stepanek.gamajun.graphql.CreateExamInput;
import app.stepanek.gamajun.graphql.UpdateExamInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.repository.ClassroomDao;
import app.stepanek.gamajun.repository.ExamDao;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExamService {
    private final AssignmentDao assignmentDao;
    private final ExamDao examDao;
    private final ExamSubmissionDao examSubmissionDao;
    private final IAuthenticationFacade authenticationFacade;
    private final ClassroomDao classroomDao;
    private final ClassroomService classroomService;

    @Autowired
    public ExamService(AssignmentDao assignmentDao, ExamDao examDao, ExamSubmissionDao examSubmissionDao, IAuthenticationFacade authenticationFacade,
                       ClassroomDao classroomDao, ClassroomService classroomService) {
        this.assignmentDao = assignmentDao;
        this.examDao = examDao;
        this.examSubmissionDao = examSubmissionDao;
        this.authenticationFacade = authenticationFacade;
        this.classroomDao = classroomDao;
        this.classroomService = classroomService;
    }

    public ExamSubmission beginExam(UUID examId) {
        log.info("User {} is starting exam with id {}", authenticationFacade.getUsername(), examId);

        var exam = examDao.findById(examId).orElseThrow(() -> new ExamNotFoundException("Exam with id '%s' was not found".formatted(examId)));

        var userExamSubmissions = examSubmissionDao.findByUser_Username(authenticationFacade.getUsername());
        if (userExamSubmissions.stream().map(ExamSubmission::getExam).anyMatch(e -> e.equals(exam)))
            throw new ExamSubmissionLockedException("You already attended exam with id '%s'".formatted(examId));

        Random rand = new Random();

        ExamSubmission examSubmission = new ExamSubmission();
        examSubmission.setAssignment(new ArrayList<>(exam.getAssignments()).get(rand.nextInt(exam.getAssignments().size())));
        examSubmission.setUser(authenticationFacade.getUser());
        examSubmission.setStartedAt(Instant.now());
        examSubmission.setExam(exam);
        examSubmission.setExamSubmissionState(ExamSubmissionState.Draft);

        return examSubmissionDao.save(examSubmission);
    }

    public Set<Exam> getOpenedExams() {
        log.info("User {} is getting opened exams", authenticationFacade.getUsername());

        Classroom classroom;
        try {
            classroom = classroomService.getClassroomByUser(authenticationFacade.getUser());
        }catch (ClassroomNotFoundException e){
            return new HashSet<>();
        }

        var exams = examDao.findByAccessibleFromLessThanEqualAndAccessibleToGreaterThanEqualAndClassroomsContains(Instant.now(), Instant.now(), classroom);
        var submissions = examSubmissionDao.findByUser_Username(authenticationFacade.getUsername());

        var attendedExams = submissions.stream().map(ExamSubmission::getExam).collect(Collectors.toSet());
        exams.removeAll(attendedExams);


        return exams;
    }

    @Transactional
    public Exam createExam(CreateExamInput createExamInput) {
        log.info("User {} is creating exam with title {}", authenticationFacade.getUsername(), createExamInput.getTitle());

        Exam exam = new Exam();

        exam.setTitle(createExamInput.getTitle());
        exam.setAuthor(authenticationFacade.getUser());
        exam.setAccessibleFrom(createExamInput.getAccessibleFrom());
        exam.setAccessibleTo(createExamInput.getAccessibleTo());
        exam.setTimeLimit(createExamInput.getTimeLimit());
        exam.setAssignments(new HashSet<>(assignmentDao.findAllById(createExamInput.getAssignmentIds())));
        exam.setClassrooms(new HashSet<>(classroomDao.findAllById(createExamInput.getClassroomIds())));

        return examDao.save(exam);
    }

    public List<Exam> findAll() {
        log.info("User {} is getting all exams", authenticationFacade.getUsername());
        return examDao.findAll();
    }

    public Exam findById(UUID examId) {
        log.info("User {} is getting exam with id {}", authenticationFacade.getUsername(), examId);

        return examDao
                .findById(examId)
                .orElseThrow(() -> new ExamNotFoundException("Exam with id '%s' was not found".formatted(examId)));
    }

    public void deleteExam(UUID examId) {
        log.info("User {} is deleting exam with id {}", authenticationFacade.getUsername(), examId);
        examDao.deleteById(examId);
    }

    public Exam update(UpdateExamInput updateExamInput) {
        log.info("User {} is updating exam with id {}", authenticationFacade.getUsername(), updateExamInput.getId());

        Exam exam = findById(updateExamInput.getId());

        exam.setTitle(updateExamInput.getTitle());
        exam.setAccessibleFrom(updateExamInput.getAccessibleFrom());
        exam.setAccessibleTo(updateExamInput.getAccessibleTo());
        exam.setTimeLimit(updateExamInput.getTimeLimit());
        exam.setAssignments(new HashSet<>(assignmentDao.findAllById(updateExamInput.getAssignmentIds())));
        exam.setClassrooms(new HashSet<>(classroomDao.findAllById(updateExamInput.getClassroomIds())));

        return examDao.save(exam);
    }
}
