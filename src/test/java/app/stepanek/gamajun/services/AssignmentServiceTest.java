package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.graphql.CreateAssignmentInput;
import app.stepanek.gamajun.graphql.UpdateAssignmentInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceTest {
    @Mock
    private AssignmentDao assignmentDao;
    @Mock
    private IAuthenticationFacade authenticationFacade;
    @InjectMocks
    private AssignmentService assignmentService;


    void mockAuthentication(){
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
    }

    @Test
    void createAssignment() {
        mockAuthentication();

        Assignment assignment = Assignment
                .builder()
                .title("title")
                .description("description")
                .xml("xml")
                .sandbox(true)
                .build();

        CreateAssignmentInput assignmentInput = CreateAssignmentInput
                .builder()
                .title("title")
                .description("description")
                .xml("xml")
                .sandbox(true)
                .build();

        when(assignmentDao.save(Mockito.any(Assignment.class))).thenReturn(assignment);
        var storedAssignment = assignmentService.createAssignment(assignmentInput);

        assertEquals(storedAssignment.getTitle(), "title");
        assertEquals(storedAssignment.getDescription(), "description");
        assertEquals(storedAssignment.getXml(), "xml");
        assertTrue(storedAssignment.isSandbox());
    }

    @Test
    void findById() {
        when(assignmentDao.findById(Mockito.any(UUID.class))).thenReturn(java.util.Optional.of(Assignment
                .builder()
                .title("title")
                .description("description")
                .xml("xml")
                .sandbox(true)
                .build()));

        var assignment = assignmentService.findById(UUID.randomUUID());

        assertEquals(assignment.getTitle(), "title");
        assertEquals(assignment.getDescription(), "description");
        assertEquals(assignment.getXml(), "xml");
        assertTrue(assignment.isSandbox());
    }

    @Test
    void updateAssignment() {
        Assignment assignment = Assignment
                .builder()
                .id(UUID.randomUUID())
                .title("title")
                .description("description")
                .xml("xml")
                .sandbox(true)
                .build();

        UpdateAssignmentInput assignmentInput = UpdateAssignmentInput
                .builder()
                .id(UUID.randomUUID())
                .title("newTitle")
                .description("newDescription")
                .xml("newXml")
                .sandbox(false)
                .build();

        when(assignmentDao.findById(Mockito.any(UUID.class))).thenReturn(java.util.Optional.of(assignment));
        when(assignmentDao.save(Mockito.any(Assignment.class))).thenAnswer(i -> i.getArguments()[0]);
        var updateAssignment = assignmentService.updateAssignment(assignmentInput);

        assertEquals(updateAssignment.getTitle(), "newTitle");
        assertEquals(updateAssignment.getDescription(), "newDescription");
        assertEquals(updateAssignment.getXml(), "newXml");
        assertFalse(updateAssignment.isSandbox());
    }

    @Test
    void sandboxAssignments() {
        when(assignmentDao.findBySandboxTrue()).thenReturn(java.util.List.of(Assignment
                .builder()
                .title("title")
                .description("description")
                .xml("xml")
                .sandbox(true)
                .build()));

        var assignment = assignmentService.sandboxAssignments();

        assertEquals(assignment.stream().findFirst().get().getTitle(), "title");
        assertEquals(assignment.stream().findFirst().get().getDescription(), "description");
        assertEquals(assignment.stream().findFirst().get().getXml(), "xml");
        assertTrue(assignment.stream().findFirst().get().isSandbox());
    }
}
