package app.stepanek.gamajun;

import app.stepanek.gamajun.domain.Admin;
import app.stepanek.gamajun.repository.AdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AdminsConfig {
    private final AdminDao administratorDao;
    Logger logger = LoggerFactory.getLogger(AdminsConfig.class);

    @Autowired
    public AdminsConfig(AdminDao administratorDao) {
        this.administratorDao = administratorDao;
    }

    @Bean
    InitializingBean sendDatabase() {
        List<Admin> administrators = new ArrayList<>();

        //administrators.add(new Admin("stepafi6"));
        administrators.add(new Admin("naplava"));

        return () -> {
            for (var administrator : administrators) {
                var exists = administratorDao.existsByUsername(administrator.getUsername());

                if (!exists) {
                    logger.info("Creating admin '{}'", administrator.getUsername());
                    administratorDao.save(administrator);
                } else
                    logger.info("Admin '{}' already exists in database", administrator.getUsername());
            }

        };
    }
}
