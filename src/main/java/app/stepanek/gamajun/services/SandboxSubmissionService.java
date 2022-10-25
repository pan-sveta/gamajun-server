package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.SandboxSubmission;
import app.stepanek.gamajun.exceptions.ExamSubmissionNotFoundException;
import app.stepanek.gamajun.exceptions.ResourceNotOwnedByCurrentUserException;
import app.stepanek.gamajun.graphql.SandboxSubmissionSubmitInput;
import app.stepanek.gamajun.repository.SandboxSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
public class SandboxSubmissionService {
    private final SandboxSubmissionDao sandboxSubmissionDao;
    private final AdminService adminService;
    private final AssignmentService assignmentService;
    @Autowired
    @Lazy
    private ValidatorService validatorService;
    private final IAuthenticationFacade authenticationFacade;


    @Autowired
    public SandboxSubmissionService(SandboxSubmissionDao examSubmissionDao, AdminService adminService, AssignmentService assignmentService, IAuthenticationFacade authenticationFacade) {
        this.sandboxSubmissionDao = examSubmissionDao;
        this.adminService = adminService;
        this.assignmentService = assignmentService;
        this.authenticationFacade = authenticationFacade;
    }

    //******
    //Create
    //******

    public SandboxSubmission save(SandboxSubmission sub) {
        return sandboxSubmissionDao.save(sub);
    }

    //****
    //Read
    //****

    @Transactional
    public SandboxSubmission findById(UUID id) {
        var sandboxSubmission = sandboxSubmissionDao.findById(id)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Sandbox submission with id %s was not found.".formatted(id)));

        if (!authenticationFacade.getUsername().equals(sandboxSubmission.getAuthor()) && !adminService.IsUserAdministrator(authenticationFacade.getUsername()))
            throw new ResourceNotOwnedByCurrentUserException("Sandbox submission '%s' is not owned by user %s".formatted(id, authenticationFacade.getUsername()));

        return sandboxSubmission;
    }

    @Transactional
    public List<SandboxSubmission> findAll() {
        return sandboxSubmissionDao.findAll();
    }

    @Transactional
    public List<SandboxSubmission> mySandboxSubmissions() {
        return sandboxSubmissionDao.findByAuthor(authenticationFacade.getUsername());
    }

    @Transactional
    public SandboxSubmission getSandboxSubmission(UUID examSubmissionId) {
        var sandboxSubmission = sandboxSubmissionDao.findById(examSubmissionId)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Sandbox submission with id %s was not found.".formatted(examSubmissionId)));

        if (!authenticationFacade.getUsername().equals(sandboxSubmission.getAuthor()) && !adminService.IsUserAdministrator(authenticationFacade.getUsername()))
            throw new ResourceNotOwnedByCurrentUserException("Sandbox submission '%s' is not owned by user %s".formatted(examSubmissionId, authenticationFacade.getUsername()));

        return sandboxSubmission;
    }

    //******
    //Delete
    //******

    @Transactional
    public boolean delete(UUID id) {
        sandboxSubmissionDao.deleteById(id);
        return true;
    }

    //*******
    //Actions
    //*******

    @Transactional
    public SandboxSubmission submitSandboxSubmission(SandboxSubmissionSubmitInput sandboxSubmissionSubmitInput) {
        var sandboxSubmission = sandboxSubmissionDao.findById(sandboxSubmissionSubmitInput.getId())
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Sandbox submission with id %s was not found.".formatted(sandboxSubmissionSubmitInput.getId())));

        if (!authenticationFacade.getUsername().equals(sandboxSubmission.getAuthor()))
            throw new ResourceNotOwnedByCurrentUserException("Sandbox submission '%s' is not owned by user %s".formatted(sandboxSubmissionSubmitInput.getId(), authenticationFacade.getUsername()));

        sandboxSubmission.setSubmittedAt(Instant.now());
        sandboxSubmission.setXml(sandboxSubmissionSubmitInput.getXml());

        var validatorReport = validatorService.validateSubmission(sandboxSubmission);
        sandboxSubmission.setValidatorReport(validatorReport);

        sandboxSubmission = sandboxSubmissionDao.save(sandboxSubmission);

        return sandboxSubmission;
    }

    public List<SandboxSubmission> findAllByAssignment(UUID assignmentId) {
        return sandboxSubmissionDao.findByAssignment_Id(assignmentId);
    }

    public SandboxSubmission createSandboxSubmission(UUID assignmentId) {
        var assignment = assignmentService.findById(assignmentId);

        SandboxSubmission submission = new SandboxSubmission();
        submission.setAssignment(assignment);
        submission.setAuthor(authenticationFacade.getUsername());
        submission.setStartedAt(Instant.now());

        return sandboxSubmissionDao.save(submission);
    }
}
