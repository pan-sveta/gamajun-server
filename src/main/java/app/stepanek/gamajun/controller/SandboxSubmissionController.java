package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.SandboxSubmission;
import app.stepanek.gamajun.graphql.SandboxSubmissionSubmitInput;
import app.stepanek.gamajun.services.SandboxSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Controller
public class SandboxSubmissionController {
    private final SandboxSubmissionService sandboxSubmissionService;

    @Autowired
    public SandboxSubmissionController(SandboxSubmissionService sandboxSubmissionService) {
        this.sandboxSubmissionService = sandboxSubmissionService;
    }

    @SchemaMapping
    public SandboxSubmission sandboxSubmission(SandboxSubmission sandboxSubmission) {
        return sandboxSubmissionService.findById(sandboxSubmission.getId());
    }

    @Secured("GAMAJUN_STUDENT")
    @QueryMapping
    public List<SandboxSubmission> sandboxSubmissions() {
        return sandboxSubmissionService.findAll();
    }

    @Secured("GAMAJUN_STUDENT")
    @QueryMapping
    public SandboxSubmission sandboxSubmissionById(@Argument UUID id) {
        return sandboxSubmissionService.findById(id);
    }

    @Secured("GAMAJUN_STUDENT")
    @QueryMapping
    public List<SandboxSubmission> mySandboxSubmissions(@Argument UUID assignmentId) {
        return sandboxSubmissionService.mySandboxSubmissions(assignmentId);
    }

    @Secured("GAMAJUN_TEACHER")
    @QueryMapping
    public List<SandboxSubmission> sandboxSubmissionsByAssignment(@Argument UUID assignmentId) {
        return sandboxSubmissionService.findAllByAssignment(assignmentId);
    }

    @Secured("GAMAJUN_TEACHER")
    @MutationMapping
    public boolean deleteSandboxSubmission(@PathVariable UUID sandboxSubmissionId) {
        return sandboxSubmissionService.delete(sandboxSubmissionId);
    }

    @Secured("GAMAJUN_STUDENT")
    @MutationMapping
    public SandboxSubmission submitSandboxSubmission(@Argument SandboxSubmissionSubmitInput input)  {
        return sandboxSubmissionService.submitSandboxSubmission(input);
    }

    @Secured("GAMAJUN_STUDENT")
    @MutationMapping
    public SandboxSubmission createSandboxSubmission(@Argument UUID assignmentId)  {
        return sandboxSubmissionService.createSandboxSubmission(assignmentId);
    }


}
