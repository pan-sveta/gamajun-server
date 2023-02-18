package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.graphql.SignUpInput;
import app.stepanek.gamajun.repository.RoleDao;
import app.stepanek.gamajun.repository.UserDao;
import app.stepanek.gamajun.services.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class AuthController {
    UserService userService;
    PasswordEncoder passwordEncoder;
    private final RoleDao roleDao;
    private final UserDao userDao;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder,
                          RoleDao roleDao,
                          UserDao userDao) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleDao = roleDao;
        this.userDao = userDao;
    }

    @SchemaMapping
    public User exam(User user) {
        return userService.findByUsername(user.getUsername());
    }

    @SchemaMapping
    public Role exam(Role role) {
        return roleDao.findByName(role.getName()).get();
    }

    @QueryMapping
    public List<Role> roles() {
        return roleDao.findAll();
    }

    @QueryMapping
    public List<User> users() {
        return userDao.findAll();
    }

    @MutationMapping
    public User signUp(@Argument SignUpInput input){
        return userService.registerUser(input);
    }
}
