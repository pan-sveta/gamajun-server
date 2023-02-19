package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.SandboxSubmission;
import app.stepanek.gamajun.graphql.SandboxSubmissionSubmitInput;
import app.stepanek.gamajun.services.SandboxSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
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

    @QueryMapping
    public List<SandboxSubmission> sandboxSubmissions() {
        return sandboxSubmissionService.findAll();
    }

    @QueryMapping
    public SandboxSubmission sandboxSubmissionById(@Argument UUID id) {
        return sandboxSubmissionService.findById(id);
    }

    @QueryMapping
    public List<SandboxSubmission> mySandboxSubmissions(@Argument UUID assignmentId) {
        return sandboxSubmissionService.mySandboxSubmissions(assignmentId);
    }

    @QueryMapping
    public List<SandboxSubmission> sandboxSubmissionsByAssignment(@Argument UUID assignmentId) {
        return sandboxSubmissionService.findAllByAssignment(assignmentId);
    }

    @MutationMapping
    public boolean deleteSandboxSubmission(@PathVariable UUID sandboxSubmissionId) {
        return sandboxSubmissionService.delete(sandboxSubmissionId);
    }

    @MutationMapping
    public SandboxSubmission submitSandboxSubmission(@Argument SandboxSubmissionSubmitInput input)  {
        return sandboxSubmissionService.submitSandboxSubmission(input);
    }

    @MutationMapping
    public SandboxSubmission createSandboxSubmission(@Argument UUID assignmentId)  {
        return sandboxSubmissionService.createSandboxSubmission(assignmentId);
    }


}
