package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.graphql.SignUpInput;
import app.stepanek.gamajun.repository.RoleDao;
import app.stepanek.gamajun.repository.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    ExamSubmissionService examSubmissionService;
    @Mock
    SandboxSubmissionService sandboxSubmissionService;
    @Mock
    UserDao userDao;
    @Mock
    RoleDao roleDao;
    @Mock
    ClassroomService classroomService;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void registerUser() {
        SignUpInput input = SignUpInput
                .builder()
                .username("test")
                .email("email")
                .password("password")
                .name("name")
                .surname("surname")
                .inviteCode("inviteCode")
                .build();

        when(classroomService.findByIdInviteCode(input.getInviteCode()))
                .thenReturn(Optional.ofNullable(
                        Classroom
                                .builder()
                                .name("name")
                                .inviteCode("inviteCode")
                                .users(new HashSet<>()).build()
                ));
        when(roleDao.findByName("GAMAJUN_STUDENT")).thenReturn(Optional.of(new Role("GAMAJUN_STUDENT")));
        when(passwordEncoder.encode(input.getPassword())).thenReturn("encodedPassword");
        when(userDao.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        var user = userService.registerUser(input);

        assertEquals(user.getUsername(), input.getUsername());
        assertEquals(user.getEmail(), input.getEmail());
        assertEquals(user.getName(), input.getName());
        assertEquals(user.getSurname(), input.getSurname());
        assertEquals(user.getPassword(), "encodedPassword");
        assertEquals(user.getRoles().size(), 1);

    }

    @Test
    void validateInviteCodeValid() {
        when(classroomService.findByIdInviteCode("inviteCode")).thenReturn(java.util.Optional.of(Classroom
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .inviteCode("inviteCode")
                .users(new HashSet<>())
                .build()));

        assertTrue(userService.validateInviteCode("inviteCode"));
    }

    @Test
    void validateInviteCodeInvalid() {
        when(classroomService.findByIdInviteCode("inviteCode2")).thenReturn(java.util.Optional.empty());

        assertFalse(userService.validateInviteCode("inviteCode2"));
    }

    @Test
    void findAll() {
    }

    @Test
    void findByUsername() {
    }
}