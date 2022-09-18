package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.repository.AssignmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    private final AssignmentDao assignmentDao;

    @Autowired
    public AssignmentController(AssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    @GetMapping
    public List<Assignment> AllAssignments() {
        return assignmentDao.findAll();
    }

    @PostMapping
    public Assignment CreatAssignment(@RequestBody Assignment assignment, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        assignment.setAuthor(principal.getAttribute("user_name"));

        return assignmentDao.save(assignment);
    }

    @DeleteMapping("/{assignmentId}")
    public void DeleteAssignment(@PathVariable UUID assignmentId) throws Exception {
        assignmentDao.deleteById(assignmentId);
    }

    @PutMapping("/{assignmentId}")
    public Assignment UpdateAssignment(@PathVariable UUID assignmentId, @RequestBody Assignment assignment) throws Exception {
        if (!assignment.getId().equals(assignmentId))
            throw new Exception("Update error");

        return assignmentDao.save(assignment);
    }

    @GetMapping("/{assignmentId}")
    public Assignment GetAssignment(@PathVariable UUID assignmentId) {
        return assignmentDao.findById(assignmentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
