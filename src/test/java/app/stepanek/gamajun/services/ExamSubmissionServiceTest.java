package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.*;
import app.stepanek.gamajun.exceptions.ResourceNotOwnedByCurrentUserException;
import app.stepanek.gamajun.graphql.ExamSubmissionCheckpointInput;
import app.stepanek.gamajun.graphql.ExamSubmissionSubmitInput;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamSubmissionServiceTest {

    @Mock
    ExamSubmissionDao examSubmissionDao;
    @Mock
    ValidatorService validatorService;
    @Mock
    IAuthenticationFacade authenticationFacade;

    @InjectMocks
    ExamSubmissionService examSubmissionService;

    User mockAuthentication() {
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


        lenient().when(authenticationFacade.getUser()).thenReturn(user);
        lenient().when(authenticationFacade.isResourceOwner(user)).thenReturn(true);
        lenient().when(authenticationFacade.getUsername()).thenReturn(user.getUsername());

        return user;
    }

    @Test
    void save() {
        ExamSubmission examSubmission = ExamSubmission.builder()
                .exam(Exam.builder()
                        .id(UUID.randomUUID())
                        .title("Test exam")
                        .build()
                )
                .build();
        examSubmission.setId(UUID.randomUUID());

        when(examSubmissionDao.save(Mockito.any(ExamSubmission.class))).thenAnswer(i -> i.getArguments()[0]);

        var saved = examSubmissionService.save(examSubmission);

        assertEquals(examSubmission, saved);
    }

    @Test
    void findById() {
        var user = mockAuthentication();

        UUID uuid = UUID.randomUUID();

        ExamSubmission examSubmission = ExamSubmission.builder()
                .exam(Exam.builder()
                        .id(UUID.randomUUID())
                        .title("Test exam")
                        .build()
                )
                .build();
        examSubmission.setUser(user);
        examSubmission.setId(uuid);

        when(examSubmissionDao.findById(uuid)).thenAnswer(i -> java.util.Optional.of(examSubmission));

        var found = examSubmissionService.findById(uuid);

        assertEquals(uuid, found.getId());
    }

    @Test
    void findAll() {
        when(examSubmissionDao.findAll()).thenAnswer(i -> new ArrayList<>(
                        List.of(
                                ExamSubmission.builder()
                                        .examSubmissionState(ExamSubmissionState.Submitted)
                                        .exam(Exam.builder()
                                                .id(UUID.randomUUID())
                                                .title("Test exam")
                                                .build()
                                        )
                                        .build(),
                                ExamSubmission.builder()
                                        .examSubmissionState(ExamSubmissionState.Draft)
                                        .exam(Exam.builder()
                                                .id(UUID.randomUUID())
                                                .title("Test exam")
                                                .build()
                                        )
                                        .build()
                        )
                )
        );

        var found = examSubmissionService.findAll();

        assertEquals(2, found.size());
    }

    @Test
    void findAllByExam() {
        when(examSubmissionDao.findByExam_Id(Mockito.any(UUID.class))).thenAnswer(i -> new ArrayList<>(
                        List.of(
                                ExamSubmission.builder()
                                        .examSubmissionState(ExamSubmissionState.Submitted)
                                        .exam(Exam.builder()
                                                .id(UUID.randomUUID())
                                                .title("Test exam")
                                                .build()
                                        )
                                        .build(),
                                ExamSubmission.builder()
                                        .examSubmissionState(ExamSubmissionState.Draft)
                                        .exam(Exam.builder()
                                                .id(UUID.randomUUID())
                                                .title("Test exam")
                                                .build()
                                        )
                                        .build()
                        )
                )
        );

        var found = examSubmissionService.findAllByExam(UUID.randomUUID());

        assertEquals(2, found.size());
    }

    @Test
    void mySubmissions() {
        mockAuthentication();
        when(examSubmissionDao.findByUser_Username("username")).thenReturn(new ArrayList<>(
                        List.of(
                                ExamSubmission.builder()
                                        .examSubmissionState(ExamSubmissionState.Submitted)
                                        .exam(Exam.builder()
                                                .id(UUID.randomUUID())
                                                .title("Test exam")
                                                .build()
                                        )
                                        .build(),
                                ExamSubmission.builder()
                                        .examSubmissionState(ExamSubmissionState.Draft)
                                        .exam(Exam.builder()
                                                .id(UUID.randomUUID())
                                                .title("Test exam")
                                                .build()
                                        )
                                        .build()
                        )
                )
        );

        var examSubmissions = examSubmissionService.mySubmissions();
        assertEquals(2, examSubmissions.size());
    }

    @Test
    void checkpointStudentExam() {
        var user = mockAuthentication();

        ExamSubmissionCheckpointInput examSubmissionCheckpointInput = ExamSubmissionCheckpointInput.builder()
                .id(UUID.randomUUID())
                .xml("<xml>foo</xml>")
                .build();


        var examSub = ExamSubmission.builder()
                .examSubmissionState(ExamSubmissionState.Draft)
                .exam(Exam.builder()
                        .id(UUID.randomUUID())
                        .title("Test exam")
                        .build()
                )
                .build();
        examSub.setUser(user);

        when(examSubmissionDao.findById(examSubmissionCheckpointInput.getId())).thenReturn(java.util.Optional.of(examSub));

        when(examSubmissionDao.save(Mockito.any(ExamSubmission.class))).thenAnswer(i -> i.getArguments()[0]);

        var examSubmission = examSubmissionService.checkpointStudentExam(examSubmissionCheckpointInput);

        assertEquals(ExamSubmissionState.Draft, examSubmission.getExamSubmissionState());
        assertEquals("<xml>foo</xml>", examSubmission.getXml());
    }

    @Test()
    void checkpointStudentExamUnauthorized() {
        ExamSubmissionCheckpointInput examSubmissionCheckpointInput = ExamSubmissionCheckpointInput.builder()
                .id(UUID.randomUUID())
                .xml("<xml>foo</xml>")
                .build();


        var examSub = ExamSubmission.builder()
                .examSubmissionState(ExamSubmissionState.Draft)
                .exam(Exam.builder()
                        .id(UUID.randomUUID())
                        .title("Test exam")
                        .build()
                )
                .build();
        examSub.setUser(User
                .builder()
                .username("username")
                .build()
        );

        when(examSubmissionDao.findById(examSubmissionCheckpointInput.getId())).thenReturn(java.util.Optional.of(examSub));

        when(authenticationFacade.getUsername()).thenReturn("badwolf");

        assertThrows(ResourceNotOwnedByCurrentUserException.class, () -> {
            examSubmissionService.checkpointStudentExam(examSubmissionCheckpointInput);
        });
    }

    @Test
    void submitStudentExam() {
        var user = mockAuthentication();

        ExamSubmissionSubmitInput examSubmissionSubmitInput = ExamSubmissionSubmitInput.builder()
                .id(UUID.randomUUID())
                .xml("<xml>foo</xml>")
                .build();


        var examSub = ExamSubmission.builder()
                .examSubmissionState(ExamSubmissionState.Draft)
                .exam(Exam.builder()
                        .id(UUID.randomUUID())
                        .title("Test exam")
                        .build()
                )
                .build();
        examSub.setUser(user);

        when(examSubmissionDao.findById(examSubmissionSubmitInput.getId())).thenReturn(java.util.Optional.of(examSub));

        when(examSubmissionDao.save(Mockito.any(ExamSubmission.class))).thenAnswer(i -> i.getArguments()[0]);

        when(validatorService.validateSubmission(Mockito.any(Submission.class))).thenReturn(
                ValidatorReport
                        .builder()
                        .build()
        );


        var examSubmission = examSubmissionService.submitStudentExam(examSubmissionSubmitInput);

        assertEquals(ExamSubmissionState.Submitted, examSubmission.getExamSubmissionState());
        assertEquals("<xml>foo</xml>", examSubmission.getXml());
    }
}