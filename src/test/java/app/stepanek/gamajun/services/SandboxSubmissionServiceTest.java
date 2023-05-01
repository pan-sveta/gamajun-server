package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.*;
import app.stepanek.gamajun.exceptions.ResourceNotOwnedByCurrentUserException;
import app.stepanek.gamajun.graphql.SandboxSubmissionSubmitInput;
import app.stepanek.gamajun.repository.SandboxSubmissionDao;
import app.stepanek.gamajun.repository.SandboxSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SandboxSubmissionServiceTest {

    @Mock
    SandboxSubmissionDao sandboxSubmissionDao;
    @Mock
    ValidatorService validatorService;
    @Mock
    IAuthenticationFacade authenticationFacade;
    @Mock
    AssignmentService assignmentService;

    @InjectMocks
    SandboxSubmissionService sandboxSubmissionService;

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
        SandboxSubmission sandboxSubmission = SandboxSubmission.builder()
                .build();
        sandboxSubmission.setId(UUID.randomUUID());

        when(sandboxSubmissionDao.save(Mockito.any(SandboxSubmission.class))).thenAnswer(i -> i.getArguments()[0]);

        var saved = sandboxSubmissionService.save(sandboxSubmission);

        assertEquals(sandboxSubmission, saved);
    }

    @Test
    void findById() {
        var user = mockAuthentication();

        UUID uuid = UUID.randomUUID();

        SandboxSubmission sandboxSubmission = SandboxSubmission.builder()
                .build();
        sandboxSubmission.setUser(user);
        sandboxSubmission.setId(uuid);

        when(sandboxSubmissionDao.findById(uuid)).thenAnswer(i -> Optional.of(sandboxSubmission));

        var found = sandboxSubmissionService.findById(uuid);

        assertEquals(uuid, found.getId());
    }

    @Test
    void findAll() {
        when(sandboxSubmissionDao.findAll()).thenAnswer(i -> new ArrayList<>(
                        List.of(
                                SandboxSubmission.builder()
                                        .build(),
                                SandboxSubmission.builder()
                                        .build()
                        )
                )
        );

        var found = sandboxSubmissionService.findAll();

        assertEquals(2, found.size());
    }

    void findAllByExam() {
        when(sandboxSubmissionDao.findByAssignment_Id(Mockito.any(UUID.class))).thenAnswer(i -> new ArrayList<>(
                        List.of(
                                SandboxSubmission.builder()
                                        .build(),
                                SandboxSubmission.builder()
                                        .build()
                        )
                )
        );

        var found = sandboxSubmissionService.findAllByAssignment(UUID.randomUUID());

        assertEquals(2, found.size());
    }

    @Test
    void mySubmissions() {
        mockAuthentication();
        when(sandboxSubmissionDao.findByAssignment_IdAndUser_Username(Mockito.any(UUID.class), eq("username"))).thenReturn(new ArrayList<>(
                        List.of(
                                SandboxSubmission.builder()
                                        .build(),
                                SandboxSubmission.builder()
                                        .build()
                        )
                )
        );

        var sandboxSubmissions = sandboxSubmissionService.mySandboxSubmissions(UUID.randomUUID());
        assertEquals(2, sandboxSubmissions.size());
    }

    @Test
    void submitStudentExam() {
        var user = mockAuthentication();

        SandboxSubmissionSubmitInput sandboxSubmissionSubmitInput = SandboxSubmissionSubmitInput.builder()
                .id(UUID.randomUUID())
                .xml("<xml>foo</xml>")
                .build();


        var examSub = SandboxSubmission.builder()
                .build();
        examSub.setUser(user);

        when(sandboxSubmissionDao.findById(sandboxSubmissionSubmitInput.getId())).thenReturn(Optional.of(examSub));

        when(sandboxSubmissionDao.save(Mockito.any(SandboxSubmission.class))).thenAnswer(i -> i.getArguments()[0]);

        when(validatorService.validateSubmission(Mockito.any(SandboxSubmission.class))).thenReturn(
                ValidatorReport
                        .builder()
                        .build()
        );


        var sandboxSubmission = sandboxSubmissionService.submitSandboxSubmission(sandboxSubmissionSubmitInput);

        assertEquals("<xml>foo</xml>", sandboxSubmission.getXml());
    }

    @Test
    void createSandboxSubmission() {
        var user = mockAuthentication();


        when(assignmentService.findById(any())).thenReturn(Assignment.builder()
                .title("title")
                .build());


        sandboxSubmissionService.createSandboxSubmission(UUID.randomUUID());
    }
}