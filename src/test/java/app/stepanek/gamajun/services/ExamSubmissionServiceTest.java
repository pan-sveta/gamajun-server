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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
        UUID uuid = UUID.randomUUID();

        ExamSubmission examSubmission = ExamSubmission.builder()
                .exam(Exam.builder()
                        .id(UUID.randomUUID())
                        .title("Test exam")
                        .build()
                )
                .build();
        examSubmission.setId(uuid);

        when(examSubmissionDao.findById(uuid)).thenAnswer(i -> java.util.Optional.of(examSubmission));

        var found = examSubmissionService.findById(uuid);

        assertEquals(uuid, found.getId());
    }

    @Test
    void findAll() {
        when(examSubmissionDao.findAll()).thenAnswer(i -> new ArrayList<ExamSubmission>(
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
        when(examSubmissionDao.findByExam_Id(Mockito.any(UUID.class))).thenAnswer(i -> new ArrayList<ExamSubmission>(
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
        when(authenticationFacade.getUsername()).thenReturn("username");
        when(examSubmissionDao.findByUser_Username("username")).thenReturn(new ArrayList<ExamSubmission>(
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

        when(authenticationFacade.getUsername()).thenReturn("username");

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
        examSub.setUser(User
                .builder()
                .username("username")
                .build()
        );

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