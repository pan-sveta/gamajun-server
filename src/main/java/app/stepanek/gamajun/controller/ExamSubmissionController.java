package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
public class ExamSubmissionController {

    private final AdminService adminService;
    private final ExamSubmissionDao examSubmissionDao;

    @Autowired
    public ExamSubmissionController(AdminService adminService, ExamSubmissionDao examSubmissionDao) {
        this.adminService = adminService;
        this.examSubmissionDao = examSubmissionDao;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
    public List<ExamSubmission> AllExamSubmissions() {
        return examSubmissionDao.findAll();
    }

    @DeleteMapping("/{examSubmissionId}")
    @PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
    public void DeleteExamSubmissions(@PathVariable UUID examSubmissionId) throws Exception {
        examSubmissionDao.deleteById(examSubmissionId);
    }

    @GetMapping("/{examSubmissionId}")
    public ExamSubmission GetExamSubmissions(@PathVariable UUID examSubmissionId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        var examSubmission = examSubmissionDao.findById(examSubmissionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String username = principal.getAttribute("user_name");
        if (!Objects.equals(username, examSubmission.getAuthor()) && !adminService.IsUserAdministrator(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return examSubmission;
    }

    @PutMapping("/{examSubmissionId}/checkpoint")
    public ExamSubmission CheckpointExamSubmissions(@PathVariable UUID examSubmissionId, @RequestBody ExamSubmission examSubmission, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) throws Exception {
        String username = principal.getAttribute("user_name");

        if (!Objects.equals(username, examSubmission.getAuthor()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (!examSubmission.getId().equals(examSubmissionId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        examSubmission.setExamSubmissionState(ExamSubmissionState.Draft);

        return examSubmissionDao.save(examSubmission);
    }

    @PutMapping("/{examSubmissionId}/submit")
    public ExamSubmission SubmitExamSubmissions(@PathVariable UUID examSubmissionId, @RequestBody ExamSubmission examSubmission, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) throws Exception {
        String username = principal.getAttribute("user_name");

        if (!Objects.equals(username, examSubmission.getAuthor()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (!examSubmission.getId().equals(examSubmissionId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        examSubmission.setExamSubmissionState(ExamSubmissionState.Submitted);

        return examSubmissionDao.save(examSubmission);
    }

}
