package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.exceptions.ClassroomNotFoundException;
import app.stepanek.gamajun.graphql.CreateClassroomInput;
import app.stepanek.gamajun.repository.ClassroomDao;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ClassroomService {
    ClassroomDao classroomDao;

    public ClassroomService(ClassroomDao classroomDao) {
        this.classroomDao = classroomDao;
    }

    @Transactional
    public Classroom createClassroom(CreateClassroomInput classroomInput) {
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
        return classroomDao.findById(id).orElseThrow(() -> new ClassroomNotFoundException("Classroom with is %s was not found".formatted(id)));
    }

    @Transactional
    public List<Classroom> findAll() {
        return classroomDao.findAll();
    }

    @Transactional
    public void deleteClassroom(UUID id) {
        classroomDao.deleteById(id);
    }

    @Transactional
    public Classroom addUserByInviteCode(String inviteCode, User user) {
        var classroom = classroomDao.findClassroomByInviteCode(inviteCode).orElseThrow(() -> new ClassroomNotFoundException("Classroom with invite code %s was not found".formatted(inviteCode)));

        var users = classroom.getUsers();
        users.add(user);

        return classroomDao.save(classroom);
    }

    @Transactional
    public Classroom addUser(UUID classroomId, User user) {
        var classroom = classroomDao.findById(classroomId).orElseThrow(() -> new ClassroomNotFoundException("Classroom with id %s was not found".formatted(classroomId)));

        var users = classroom.getUsers();
        users.add(user);

        return classroomDao.save(classroom);
    }

    @Transactional
    public Classroom removeUserFromClassroom(UUID classroomId, String username) {
        var classroom = classroomDao.findById(classroomId).orElseThrow(() -> new ClassroomNotFoundException("Classroom with id %s was not found".formatted(classroomId)));
        var users = classroom.getUsers();
        var wasRemoved = users.removeIf(x -> Objects.equals(x.getUsername(), username));

        if (!wasRemoved)
            throw new UsernameNotFoundException("User with username %s was not found".formatted(username));

        return classroomDao.save(classroom);
    }

    @Transactional

    public boolean validateInviteCode(String inviteCode) {
        var classroom = classroomDao.findClassroomByInviteCode(inviteCode).orElse(null);

        return classroom != null;
    }
}
