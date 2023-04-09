package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.graphql.CreateClassroomInput;
import app.stepanek.gamajun.repository.ClassroomDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceTest {
    @Mock
    ClassroomDao classroomDao;

    @Mock
    IAuthenticationFacade authenticationFacade;

    @InjectMocks
    ClassroomService classroomService;

    @Test
    void createClassroom() {
        Classroom classroom = Classroom
                .builder()
                .name("name")
                .inviteCode("inviteCode")
                .users(new HashSet<>())
                .build();

        CreateClassroomInput classroomInput = CreateClassroomInput
                .builder()
                .name("name")
                .inviteCode("inviteCode")
                .build();

        when(classroomDao.save(Mockito.any(Classroom.class))).thenReturn(classroom);
        var storedClassroom = classroomService.createClassroom(classroomInput);

        assertEquals(storedClassroom.getName(), "name");
        assertEquals(storedClassroom.getInviteCode(), "inviteCode");
        assertEquals(storedClassroom.getUsers().size(), 0);
    }

    @Test
    void findById() {
        when(classroomDao.findById(Mockito.any(UUID.class))).thenReturn(java.util.Optional.of(Classroom
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .inviteCode("inviteCode")
                .build()));

        var classroom = classroomService.findById(UUID.randomUUID());

        assertEquals(classroom.getName(), "name");
        assertEquals(classroom.getInviteCode(), "inviteCode");
    }

    @Test
    void findAll() {
        when(classroomDao.findAll()).thenReturn(java.util.List.of(Classroom
                        .builder()
                        .id(UUID.randomUUID())
                        .name("name")
                        .inviteCode("inviteCode")
                        .build(),
                Classroom
                        .builder()
                        .id(UUID.randomUUID())
                        .name("name2")
                        .inviteCode("inviteCode2")
                        .build()));

        var classrooms = classroomService.findAll();

        assertEquals(classrooms.size(), 2);
    }

    @Test
    void addUserByInviteCode() {
        when(classroomDao.findClassroomByInviteCode(Mockito.any(String.class))).thenReturn(java.util.Optional.of(Classroom
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .inviteCode("inviteCode")
                .users(new HashSet<>(java.util.Set.of(User
                        .builder()
                        .username("username")
                        .password("password")
                        .email("email")
                        .name("name")
                        .surname("surname")
                        .build())))
                .build()));

        when(classroomDao.save(Mockito.any(Classroom.class))).thenAnswer(i -> i.getArguments()[0]);

        var classroom = classroomService
                .addUserByInviteCode("inviteCode", User
                        .builder()
                        .username("username2")
                        .password("password2")
                        .email("email2")
                        .name("name2")
                        .surname("surname2")
                        .build()
                );


        assertEquals(classroom.getUsers().size(), 2);
    }

    @Test
    void addUser() {
        when(classroomDao.findById(Mockito.any(UUID.class))).thenReturn(java.util.Optional.of(Classroom
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .inviteCode("inviteCode")
                .users(new HashSet<>(java.util.Set.of(User
                        .builder()
                        .username("username")
                        .password("password")
                        .email("email")
                        .name("name")
                        .surname("surname")
                        .build())))
                .build()));

        when(classroomDao.save(Mockito.any(Classroom.class))).thenAnswer(i -> i.getArguments()[0]);

        var classroom = classroomService
                .addUser(UUID.randomUUID(), User
                        .builder()
                        .username("username2")
                        .password("password2")
                        .email("email2")
                        .name("name2")
                        .surname("surname2")
                        .build()
                );


        assertEquals(classroom.getUsers().size(), 2);
    }

    @Test
    void removeUserFromClassroom() {
        when(classroomDao.findById(Mockito.any(UUID.class))).thenReturn(java.util.Optional.of(Classroom
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .inviteCode("inviteCode")
                .users(new HashSet<>(java.util.Set.of(User
                        .builder()
                        .username("username")
                        .password("password")
                        .email("email")
                        .name("name")
                        .surname("surname")
                        .build())))
                .build()));

        when(classroomDao.save(Mockito.any(Classroom.class))).thenAnswer(i -> i.getArguments()[0]);

        var classroom = classroomService.removeUserFromClassroom(UUID.randomUUID(), "username");

        assertEquals(classroom.getUsers().size(), 0);
    }

    @Test
    void validateInviteCodeValid() {
        when(classroomDao.findClassroomByInviteCode("inviteCode")).thenReturn(java.util.Optional.of(Classroom
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .inviteCode("inviteCode")
                .users(new HashSet<>())
                .build()));

        assertNotNull(classroomService.findByIdInviteCode("inviteCode").get());
    }
    @Test
    void getClassroomByUser() {
        var user = User
                .builder()
                .username("username")
                .password("password")
                .email("email")
                .name("name")
                .surname("surname")
                .build();

        when(classroomDao.findClassroomByUsersContains(Mockito.any(User.class))).thenReturn(java.util.Optional.of(Classroom
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .inviteCode("inviteCode")
                .users(java.util.Set.of(user))
                .build()));

        var classrooms = classroomService.getClassroomByUser(user);

        assertNotNull(classrooms);
    }
}