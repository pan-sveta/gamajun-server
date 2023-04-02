package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.exceptions.ClassroomNotFoundException;
import app.stepanek.gamajun.graphql.CreateClassroomInput;
import app.stepanek.gamajun.repository.ClassroomDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ClassroomService {
    ClassroomDao classroomDao;
    IAuthenticationFacade authenticationFacade;

    @Value("${ADMIN_CODE}")
    private String adminCode;

    public ClassroomService(ClassroomDao classroomDao, IAuthenticationFacade authenticationFacade) {
        this.classroomDao = classroomDao;
        this.authenticationFacade = authenticationFacade;
    }

    @Transactional
    public Classroom createClassroom(CreateClassroomInput classroomInput) {
        log.info("User {} is creating classroom with name: {}", authenticationFacade.getUsername(), classroomInput.getName());

        if (classroomInput.getInviteCode().equals(adminCode))
            throw new IllegalArgumentException("Kód pozvánky se nesmí shodovat s tajným kódem!".formatted());

        Classroom classroom = new Classroom();

        classroom.setId(UUID.randomUUID());
        classroom.setName(classroomInput.getName());
        classroom.setInviteCode(classroomInput.getInviteCode());
        classroom.setUsers(new HashSet<>());

        classroom = classroomDao.save(classroom);

        return classroom;
    }

    @Transactional
    public Classroom findById(UUID id) {
        log.info("User {} is finding classroom with id: {}", authenticationFacade.getUsername(), id);
        return classroomDao.findById(id).orElseThrow(() -> new ClassroomNotFoundException("Classroom with is %s was not found".formatted(id)));
    }

    @Transactional
    public List<Classroom> findAll() {
        log.info("User {} is finding all classrooms", authenticationFacade.getUsername());

        return classroomDao.findAll();
    }

    @Transactional
    public void deleteClassroom(UUID id) {
        log.info("User {} is deleting classroom with id: {}", authenticationFacade.getUsername(), id);
        classroomDao.deleteById(id);
    }

    @Transactional
    public Classroom addUserByInviteCode(String inviteCode, User user) {
        log.info("Adding user with username {} to classroom with invite code {}", user.getUsername(), inviteCode);

        var classroom = classroomDao.findClassroomByInviteCode(inviteCode).orElseThrow(() -> new ClassroomNotFoundException("Classroom with invite code %s was not found".formatted(inviteCode)));

        var users = classroom.getUsers();
        users.add(user);

        return classroomDao.save(classroom);
    }

    @Transactional
    public Classroom addUser(UUID classroomId, User user) {
        log.info("User {} is adding user with username {} to classroom with id {}", authenticationFacade.getUsername(), user.getUsername(), classroomId);

        var classroom = classroomDao.findById(classroomId).orElseThrow(() -> new ClassroomNotFoundException("Classroom with id %s was not found".formatted(classroomId)));

        var users = classroom.getUsers();
        users.add(user);

        return classroomDao.save(classroom);
    }

    @Transactional
    public Classroom removeUserFromClassroom(UUID classroomId, String username) {
        log.info("User {} is removing user with username {} from classroom with id {}", authenticationFacade.getUsername(), username, classroomId);

        var classroom = classroomDao.findById(classroomId).orElseThrow(() -> new ClassroomNotFoundException("Classroom with id %s was not found".formatted(classroomId)));
        var users = classroom.getUsers();
        var wasRemoved = users.removeIf(x -> Objects.equals(x.getUsername(), username));

        if (!wasRemoved){
            log.error("User with username {} was not found in classroom with id {}", username, classroomId);
            throw new UsernameNotFoundException("User with username %s was not found".formatted(username));
        }

        return classroomDao.save(classroom);
    }

    @Transactional
    public Classroom getClassroomByUser(User user) {
        log.info("User {} is finding classroom by user with username {}", authenticationFacade.getUsername(), user.getUsername());

        return classroomDao.findClassroomByUsersContains(user).orElseThrow(() -> new ClassroomNotFoundException("Classroom containing user %s was not found".formatted(user.getUsername())));
    }

    public Optional<Classroom> findByIdInviteCode(String inviteCode) {
        log.info("Finding classroom by invite code {}", inviteCode);

        return classroomDao.findClassroomByInviteCode(inviteCode);
    }
}
