package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.graphql.CreateAssignmentInput;
import app.stepanek.gamajun.graphql.UpdateAssignmentInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Controller
//@PreAuthorize("hasRole('GAMAJUN_TEACHER')")
public class AssignmentController  {
    private final AssignmentService assignmentService;
    private final AssignmentDao assignmentDao;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, AssignmentDao assignmentDao) {
        this.assignmentService = assignmentService;
        this.assignmentDao = assignmentDao;
    }

    @SchemaMapping
    public Assignment assignment(Assignment assignment) {
        return assignmentService.findById(assignment.getId());
    }

    @MutationMapping
    public Assignment createAssignment(@Argument CreateAssignmentInput input) {
       return assignmentService.createAssignment(input);
    }

    @QueryMapping
    public List<Assignment> assignments() {
        return assignmentDao.findAll();
    }

    @QueryMapping
    public Assignment assignmentById(@Argument UUID id) {
        return assignmentDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @QueryMapping
    public List<Assignment> sandboxAssignments() {
        return assignmentService.sandboxAssignments();
    }

    @MutationMapping
    public Assignment updateAssignment(@Argument UpdateAssignmentInput input) {
        return assignmentService.updateAssignment(input);
    }

    @MutationMapping
    public boolean deleteAssignment(@Argument UUID id)  {
        assignmentDao.deleteById(id);
        return true;
    }


}
