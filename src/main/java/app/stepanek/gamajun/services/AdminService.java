package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Admin;
import app.stepanek.gamajun.exceptions.AdminNotFoundException;
import app.stepanek.gamajun.repository.AdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AdminService {
    private final AdminDao administratorDao;
    Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    public AdminService(AdminDao administratorDao) {
        this.administratorDao = administratorDao;
    }

    @Transactional
    public boolean IsUserAdministrator(String username) {
        return administratorDao.existsByUsername(username);
    }

    @Transactional
    public Admin createAdmin(Admin admin) {
        return administratorDao.save(admin);
    }

    @Transactional
    public List<Admin> getAllAdmins() {
        return administratorDao.findAll();
    }

    @Transactional
    public void deleteAdmin(String username) {
        administratorDao.deleteById(username);
    }

    @Transactional
    public Admin getAdmin(String username) {
        return administratorDao.findById(username)
                .orElseThrow(() -> new AdminNotFoundException("User with username '%s' is not an admin".formatted(username)));
    }
}
