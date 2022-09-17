package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.repository.AssignmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    private final AssignmentDao assignmentDao;

    @Autowired
    public AssignmentController(AssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    @GetMapping
    public List<Assignment> AllAssignment() {
        return assignmentDao.findAll();
    }

    @PostMapping
    public Assignment CreatAssignment(@RequestBody Assignment assignment, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        assignment.setAuthor(principal.getAttribute("user_name"));

        return assignmentDao.save(assignment);
    }

}
