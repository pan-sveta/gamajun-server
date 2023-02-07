package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Admin;
import app.stepanek.gamajun.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public List<Admin> AllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping(value = "/{username}")
    public Admin GetAdmin(@PathVariable String username) {
        return adminService.getAdmin(username);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
    public Admin CreatAdmin(@RequestBody Admin assignment) {
        return adminService.createAdmin(assignment);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
    public void DeleteAdmin(@PathVariable String username) throws Exception {
        adminService.deleteAdmin(username);
    }
}
