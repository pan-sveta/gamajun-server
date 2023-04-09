package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.*;
import app.stepanek.gamajun.graphql.CreateAssignmentInput;
import app.stepanek.gamajun.graphql.CreateExamInput;
import app.stepanek.gamajun.graphql.UpdateExamInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.repository.ClassroomDao;
import app.stepanek.gamajun.repository.ExamDao;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {
    @Mock
    private AssignmentDao assignmentDao;
    @Mock
    private ExamDao examDao;
    @Mock
    private ExamSubmissionDao examSubmissionDao;
    @Mock
    private IAuthenticationFacade authenticationFacade;
    @Mock
    private ClassroomDao classroomDao;
    @Mock
    private ClassroomService classroomService;

    @InjectMocks
    private ExamService examService;

    void mockAuthentication() {
        Set<Role> roles = new HashSet<>();

        var studentRole = new Role("GAMAJUN_STUDENT");
        var teacherRole = new Role("GAMAJUN_TEACHER");
        roles.add(studentRole);
        roles.add(teacherRole);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        user.setName("name");
        user.setSurname("surname");
        user.setRoles(roles);


        when(authenticationFacade.getUser()).thenReturn(user);
        lenient().when(authenticationFacade.getUsername()).thenReturn(user.getUsername());
    }

    @Test
    void beginExam() {
        mockAuthentication();

        when(examDao.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(
                                Exam
                                        .builder()
                                        .id(UUID.randomUUID())
                                        .title("title")
                                        .assignments(Set.of(
                                                Assignment
                                                        .builder()
                                                        .id(UUID.randomUUID())
                                                        .title("title")
                                                        .build(),
                                                Assignment
                                                        .builder()
                                                        .id(UUID.randomUUID())
                                                        .title("title2")
                                                        .build()
                                        ))
                                        .build()
                        )
                );

        when(examSubmissionDao.findByUser_Username("username"))
                .thenReturn(new ArrayList<>());

        when(examSubmissionDao.save(Mockito.any(ExamSubmission.class))).thenAnswer(i -> i.getArguments()[0]);
        var examSubmission = examService.beginExam(UUID.randomUUID());

        assertEquals(examSubmission.getExamSubmissionState(), ExamSubmissionState.Draft);
        assertEquals(examSubmission.getUser().getUsername(), "username");
    }

    @Test
    void getOpenedExams() {
        mockAuthentication();

        when(classroomService.getClassroomByUser(Mockito.any(User.class))).thenReturn(
                Classroom
                        .builder()
                        .id(UUID.randomUUID())
                        .name("name")
                        .build()
        );

        var exam1 = Exam
                .builder()
                .id(UUID.randomUUID())
                .title("title")
                .build();

        var exam2 = Exam
                .builder()
                .id(UUID.randomUUID())
                .title("title2")
                .build();

        when(examDao.findByAccessibleFromLessThanEqualAndAccessibleToGreaterThanEqualAndClassroomsContains(Mockito.any(Instant.class), Mockito.any(Instant.class), Mockito.any(Classroom.class)))
                .thenReturn(Set.of(
                        exam1,
                        exam2
                ));

        when(examSubmissionDao.findByUser_Username("username"))
                .thenReturn(new ArrayList<>(List.of(
                        ExamSubmission.builder()
                                .exam(exam1).build())));

        var exams = examService.getOpenedExams();

        assertEquals(exams.size(), 1);
    }

    @Test
    void createExam() {
        mockAuthentication();

        Exam exam = Exam
                .builder()
                .title("title")
                .accessibleFrom(Instant.now())
                .accessibleTo(Instant.now())
                .timeLimit(10)
                .author(User
                        .builder()
                        .username("username")
                        .build())
                .assignments(Set.of(
                        Assignment
                                .builder()
                                .id(UUID.randomUUID())
                                .title("title")
                                .build(),
                        Assignment
                                .builder()
                                .id(UUID.randomUUID())
                                .title("title2")
                                .build()
                ))
                .classrooms(Set.of(
                        Classroom
                                .builder()
                                .id(UUID.randomUUID())
                                .name("name")
                                .build()
                ))
                .build();

        CreateExamInput createExamInput = CreateExamInput
                .builder()
                .title("title")
                .accessibleFrom(Instant.now())
                .accessibleTo(Instant.now())
                .timeLimit(10)
                .assignmentIds(List.of(UUID.randomUUID()))
                .classroomIds(List.of(UUID.randomUUID()))
                .build();

        when(examDao.save(Mockito.any(Exam.class))).thenReturn(exam);
        var storedAssignment = examService.createExam(createExamInput);

        assertEquals(storedAssignment.getTitle(), "title");
        assertEquals(storedAssignment.getAuthor().getUsername(), "username");
        assertNotNull(storedAssignment.getAccessibleFrom());
        assertNotNull(storedAssignment.getAccessibleTo());
        assertEquals(storedAssignment.getTimeLimit(), 10);
        assertEquals(storedAssignment.getAssignments().size(), 2);
        assertEquals(storedAssignment.getClassrooms().size(), 1);
    }

    @Test
    void findAll() {
        when(examDao.findAll()).thenReturn(new ArrayList<>(List.of(
                Exam
                        .builder()
                        .id(UUID.randomUUID())
                        .title("title")
                        .build(),
                Exam
                        .builder()
                        .id(UUID.randomUUID())
                        .title("title2")
                        .build()
        )));

        var exams = examService.findAll();

        assertEquals(exams.size(), 2);
    }

    @Test
    void findById() {
        when(examDao.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(
                Exam
                        .builder()
                        .id(UUID.randomUUID())
                        .title("title")
                        .build()
        ));

        var exam = examService.findById(UUID.randomUUID());

        assertEquals(exam.getTitle(), "title");
    }

    @Test
    void update() {
        var assignment = Assignment
                .builder()
                .id(UUID.randomUUID())
                .title("title")
                .build();

        var classroom = Classroom
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .build();

        Exam exam = Exam
                .builder()
                .id(UUID.randomUUID())
                .title("title")
                .accessibleFrom(Instant.now())
                .accessibleTo(Instant.now())
                .timeLimit(10)
                .author(User
                        .builder()
                        .username("username")
                        .build())
                .assignments(Set.of(
                        assignment
                ))
                .classrooms(Set.of(
                        classroom
                ))
                .build();

        when(examDao.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(exam));
        when(examDao.save(Mockito.any(Exam.class))).thenAnswer(i -> i.getArguments()[0]);

        UpdateExamInput updateExamInput = UpdateExamInput
                .builder()
                .id(UUID.randomUUID())
                .title("newTitle")
                .accessibleFrom(Instant.now())
                .accessibleTo(Instant.now())
                .timeLimit(20)
                .assignmentIds(List.of(UUID.randomUUID()))
                .classroomIds(List.of(UUID.randomUUID()))
                .build();

        var updatedExam = examService.update(updateExamInput);

        assertEquals(updatedExam.getTitle(), "newTitle");
        assertEquals(updatedExam.getTimeLimit(), 20);
    }
}