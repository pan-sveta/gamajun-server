package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.SandboxSubmission;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.exceptions.ExamSubmissionNotFoundException;
import app.stepanek.gamajun.exceptions.ResourceNotOwnedByCurrentUserException;
import app.stepanek.gamajun.graphql.SandboxSubmissionSubmitInput;
import app.stepanek.gamajun.repository.SandboxSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class SandboxSubmissionService {
    private final SandboxSubmissionDao sandboxSubmissionDao;
    private final AssignmentService assignmentService;
    @Autowired
    @Lazy
    private ValidatorService validatorService;
    private final IAuthenticationFacade authenticationFacade;


    @Autowired
    public SandboxSubmissionService(SandboxSubmissionDao examSubmissionDao, AssignmentService assignmentService, IAuthenticationFacade authenticationFacade) {
        this.sandboxSubmissionDao = examSubmissionDao;
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
        log.info("User {} is finding sandbox submission with id {}", authenticationFacade.getUsername(), id);
        var sandboxSubmission = sandboxSubmissionDao.findById(id)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Sandbox submission with id %s was not found.".formatted(id)));

        if (!authenticationFacade.isResourceOwner(sandboxSubmission.getUser())) {
            log.error("User {} is not the owner of sandbox submission with id {}", authenticationFacade.getUsername(), id);
            throw new ResourceNotOwnedByCurrentUserException("Sandbox submission '%s' is not owned by user %s".formatted(id, authenticationFacade.getUsername()));
        }

        return sandboxSubmission;
    }

    @Transactional
    public List<SandboxSubmission> findAll() {
        log.info("User {} is finding all sandbox submissions", authenticationFacade.getUsername());
        return sandboxSubmissionDao.findAll();
    }

    @Transactional
    public List<SandboxSubmission> mySandboxSubmissions(UUID assignmentId) {
        log.info("User {} is finding all sandbox submissions for assignment {}", authenticationFacade.getUsername(), assignmentId);
        return sandboxSubmissionDao.findByAssignment_IdAndUser_Username(assignmentId, authenticationFacade.getUsername());
    }

    @Transactional
    public SandboxSubmission getSandboxSubmission(UUID examSubmissionId) {
        log.info("User {} is finding sandbox submission with id {}", authenticationFacade.getUsername(), examSubmissionId);

        var sandboxSubmission = sandboxSubmissionDao.findById(examSubmissionId)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Sandbox submission with id %s was not found.".formatted(examSubmissionId)));

        if (!authenticationFacade.isResourceOwner(sandboxSubmission.getUser())) {
            log.error("User {} is not the owner of sandbox submission with id {}", authenticationFacade.getUsername(), examSubmissionId);
            throw new ResourceNotOwnedByCurrentUserException("Sandbox submission '%s' is not owned by user %s".formatted(examSubmissionId, authenticationFacade.getUsername()));
        }

        return sandboxSubmission;
    }

    @Transactional
    public List<SandboxSubmission> findAllByAssignment(UUID assignmentId) {
        log.info("User {} is finding all sandbox submissions for assignment {}", authenticationFacade.getUsername(), assignmentId);

        return sandboxSubmissionDao.findByAssignment_Id(assignmentId);
    }

    @Transactional
    public List<SandboxSubmission> findAllByAssignmentAndAuthor(UUID assignmentId) {
        log.info("User {} is finding all sandbox submissions for assignment {}", authenticationFacade.getUsername(), assignmentId);

        return sandboxSubmissionDao.findByAssignment_Id(assignmentId);
    }

    //******
    //Delete
    //******

    @Transactional
    public void delete(UUID id) {
        log.info("User {} is deleting sandbox submission with id {}", authenticationFacade.getUsername(), id);

        sandboxSubmissionDao.deleteById(id);
    }

    @Transactional
    public void deleteByUser(User user) {
        log.info("User {} is deleting all sandbox submissions for user {}", authenticationFacade.getUsername(), user.getUsername());
        sandboxSubmissionDao.deleteExamSubmissionsByUser(user);
    }

    //*******
    //Actions
    //*******

    @Transactional
    public SandboxSubmission submitSandboxSubmission(SandboxSubmissionSubmitInput sandboxSubmissionSubmitInput) {
        log.info("User {} is submitting sandbox submission with id {}", authenticationFacade.getUsername(), sandboxSubmissionSubmitInput.getId());

        var sandboxSubmission = sandboxSubmissionDao.findById(sandboxSubmissionSubmitInput.getId())
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Sandbox submission with id %s was not found.".formatted(sandboxSubmissionSubmitInput.getId())));

        if (!authenticationFacade.isResourceOwner(sandboxSubmission.getUser())) {
            log.error("User {} is not the owner of sandbox submission with id {}", authenticationFacade.getUsername(), sandboxSubmissionSubmitInput.getId());
            throw new ResourceNotOwnedByCurrentUserException("Sandbox submission '%s' is not owned by user %s".formatted(sandboxSubmissionSubmitInput.getId(), authenticationFacade.getUsername()));
        }

        sandboxSubmission.setSubmittedAt(Instant.now());
        sandboxSubmission.setXml(sandboxSubmissionSubmitInput.getXml());

        var validatorReport = validatorService.validateSubmission(sandboxSubmission);
        sandboxSubmission.setValidatorReport(validatorReport);

        sandboxSubmission = sandboxSubmissionDao.save(sandboxSubmission);

        return sandboxSubmission;
    }

    public SandboxSubmission createSandboxSubmission(UUID assignmentId) {
        log.info("User {} is creating sandbox submission for assignment {}", authenticationFacade.getUsername(), assignmentId);

        var assignment = assignmentService.findById(assignmentId);

        SandboxSubmission submission = new SandboxSubmission();
        submission.setAssignment(assignment);
        submission.setUser(authenticationFacade.getUser());
        submission.setStartedAt(Instant.now());

        return sandboxSubmissionDao.save(submission);
    }
}
