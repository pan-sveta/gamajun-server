package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.exceptions.ClassroomNotFoundException;
import app.stepanek.gamajun.graphql.SignUpInput;
import app.stepanek.gamajun.repository.ClassroomDao;
import app.stepanek.gamajun.repository.RoleDao;
import app.stepanek.gamajun.repository.UserDao;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService {

    UserDao userDao;
    RoleDao roleDao;
    ClassroomService classroomService;
    PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, RoleDao roleDao, ClassroomService classroomService, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.classroomService = classroomService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(SignUpInput input) {
        User user = new User();

        if (!classroomService.validateInviteCode(input.getInviteCode()))
            throw new ClassroomNotFoundException("Classroom with code %s was not found".formatted(input.getInviteCode()));

        user.setName(input.getName());
        user.setSurname(input.getSurname());
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(roleDao.findByName("GAMAJUN_STUDENT").get());

        if (Objects.equals(user.getUsername(), "stepafi6"))
            roles.add(roleDao.findByName("GAMAJUN_TEACHER").get());

        user.setRoles(roles);

        user = userDao.save(user);

        classroomService.addUserByInviteCode(input.getInviteCode(), user);

        return user;
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userDao.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        userDao.delete(user);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}