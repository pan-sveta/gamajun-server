package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.exceptions.ClassroomNotFoundException;
import app.stepanek.gamajun.exceptions.UserAlreadyExistsException;
import app.stepanek.gamajun.graphql.SignUpInput;
import app.stepanek.gamajun.repository.ClassroomDao;
import app.stepanek.gamajun.repository.RoleDao;
import app.stepanek.gamajun.repository.UserDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final ExamSubmissionService examSubmissionService;
    private final SandboxSubmissionService sandboxSubmissionService;
    UserDao userDao;
    RoleDao roleDao;
    ClassroomService classroomService;
    PasswordEncoder passwordEncoder;
    IAuthenticationFacade authenticationFacade;

    @Value("${ADMIN_CODE}")
    private String adminCode;


    public UserService(UserDao userDao, RoleDao roleDao, ClassroomService classroomService, PasswordEncoder passwordEncoder, ExamSubmissionService examSubmissionService, SandboxSubmissionService sandboxSubmissionService, IAuthenticationFacade authenticationFacade) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.classroomService = classroomService;
        this.passwordEncoder = passwordEncoder;
        this.examSubmissionService = examSubmissionService;
        this.sandboxSubmissionService = sandboxSubmissionService;
        this.authenticationFacade = authenticationFacade;
    }

    @Transactional
    public User registerUser(SignUpInput input) {
        User user = new User();

        if (!validateInviteCode(input.getInviteCode()))
            throw new ClassroomNotFoundException("Classroom with code %s was not found".formatted(input.getInviteCode()));

        if (userDao.findByUsername(input.getUsername()).isPresent())
            throw new UserAlreadyExistsException("Uživatel se jménem %s již existuje".formatted(input.getUsername()));

        user.setName(input.getName());
        user.setSurname(input.getSurname());
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        Set<Role> roles = new HashSet<>();

        if (input.getInviteCode().equals(adminCode))
            roles.add(roleDao.findByName("GAMAJUN_TEACHER").get());
        else
            roles.add(roleDao.findByName("GAMAJUN_STUDENT").get());

        user.setRoles(roles);

        user = userDao.save(user);

        if (!input.getInviteCode().equals(adminCode))
            classroomService.addUserByInviteCode(input.getInviteCode(), user);

        return user;
    }

    @Transactional
    public void deleteUser(String username) {
        log.info("User {} is deleting user {}", authenticationFacade.getUsername(), username);

        User user = userDao.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        examSubmissionService.deleteByUser(user);
        sandboxSubmissionService.deleteByUser(user);

        userDao.delete(user);
    }

    public List<User> findAll() {
        log.info("User {} is getting all users", authenticationFacade.getUsername());
        return userDao.findAll();
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional
    public boolean validateInviteCode(String inviteCode) {
        log.info("Validating invite code {}", inviteCode);

        var classroom = classroomService.findByIdInviteCode(inviteCode);

        return (classroom.isPresent()) || inviteCode.equals(adminCode);
    }
}
