package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.graphql.CreateAssignmentInput;
import app.stepanek.gamajun.graphql.UpdateAssignmentInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.services.AssignmentService;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
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
@PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
public class AssignmentController  {
    private final AssignmentService assignmentService;
    private final AssignmentDao assignmentDao;
    private final IAuthenticationFacade authenticationFacade;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, AssignmentDao assignmentDao, IAuthenticationFacade authenticationFacade) {
        this.assignmentService = assignmentService;
        this.assignmentDao = assignmentDao;
        this.authenticationFacade = authenticationFacade;
    }

    @SchemaMapping
    public Assignment assignment(Assignment assignment) {
        return assignmentDao.findById(assignment.getId()).orElseThrow();
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

    @MutationMapping
    public Assignment updateAssignment(@Argument UpdateAssignmentInput input) {
        return assignmentService.updateAssignment(input);
    }

    @MutationMapping
    public void deleteAssignment(@Argument UUID id)  {
        assignmentDao.deleteById(id);
    }


}
