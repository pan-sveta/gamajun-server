package app.stepanek.gamajun.security;

import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.repository.RoleDao;
import app.stepanek.gamajun.repository.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class AuthorizationInitializer {
    private final RoleDao roleDao;
    private final Logger logger = LoggerFactory.getLogger(AuthorizationInitializer.class);

    public AuthorizationInitializer(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Bean
    InitializingBean seedRoles() {
        Set<Role> roles = new HashSet<>();

        var studentRole = new Role("GAMAJUN_STUDENT");
        var teacherRole = new Role("GAMAJUN_TEACHER");
        roles.add(studentRole);
        roles.add(teacherRole);

        return () -> {
            for (var role : roles) {
                var exists = roleDao.existsByName(role.getName());

                if (!exists) {
                    logger.info("Creating role '{}'", role.getName());
                    roleDao.save(role);
                } else
                    logger.info("Role '{}' already exists in database", role.getName());
            }
        };
    }
}