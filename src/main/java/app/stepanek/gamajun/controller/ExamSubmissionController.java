package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.command.ExamSubmissionCheckpointCommand;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.command.ExamSubmissionSubmitCommand;
import app.stepanek.gamajun.dto.StudentExamSubmissionDTO;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.services.AdminService;
import app.stepanek.gamajun.services.ExamSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
public class ExamSubmissionController {

    private final ExamSubmissionService examSubmissionService;
    private final ExamSubmissionDao examSubmissionDao;

    @Autowired
    public ExamSubmissionController(ExamSubmissionService examSubmissionService, ExamSubmissionDao examSubmissionDao) {
        this.examSubmissionService = examSubmissionService;
        this.examSubmissionDao = examSubmissionDao;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
    public List<ExamSubmission> AllExamSubmissions() {
        return examSubmissionDao.findAll();
    }

    @DeleteMapping("/{examSubmissionId}")
    @PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
    public void DeleteExamSubmissions(@PathVariable UUID examSubmissionId) {
        examSubmissionDao.deleteById(examSubmissionId);
    }

    @GetMapping("/{examSubmissionId}")
    public StudentExamSubmissionDTO GetExamSubmissions(@PathVariable UUID examSubmissionId) {
        return examSubmissionService.getExamSubmission(examSubmissionId);
    }

    @GetMapping("/my")
    public List<StudentExamSubmissionDTO> MyExamSubmissions() {
        return examSubmissionService.mySubmissions();
    }

    @PutMapping("/{examSubmissionId}/checkpoint")
    public StudentExamSubmissionDTO CheckpointExamSubmissions(@PathVariable UUID examSubmissionId, @RequestBody ExamSubmissionCheckpointCommand checkpointCommand) {
        return examSubmissionService.checkpointStudentExam(examSubmissionId, checkpointCommand);
    }

    @PutMapping("/{examSubmissionId}/submit")
    public StudentExamSubmissionDTO SubmitExamSubmissions(@PathVariable UUID examSubmissionId, @RequestBody ExamSubmissionSubmitCommand submitCommand)  {
        return examSubmissionService.submitStudentExam(examSubmissionId, submitCommand);
    }

}
