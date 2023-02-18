package app.stepanek.gamajun.security;

import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.repository.RoleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RolesInitializer {
    private final RoleDao roleDao;
    Logger logger = LoggerFactory.getLogger(RolesInitializer.class);

    public RolesInitializer(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Bean
    InitializingBean seedRoles() {
        List<Role> roles = new ArrayList<>();

        roles.add(new Role("GAMAJUN_STUDENT"));
        roles.add(new Role("GAMAJUN_TEACHER"));

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