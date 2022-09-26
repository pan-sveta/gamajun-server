package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import app.stepanek.gamajun.repository.ExamDao;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exams")
@PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
public class ExamController {
    private final ExamDao examDao;
    private final ExamSubmissionDao examSubmissionDao;

    @Autowired
    public ExamController(ExamDao ExamDao, ExamSubmissionDao examSubmissionDao) {
        this.examDao = ExamDao;
        this.examSubmissionDao = examSubmissionDao;
    }

    @GetMapping
    public List<Exam> AllExams() {
        return examDao.findAll();
    }

    @PostMapping
    public Exam CreatExam(@RequestBody Exam Exam, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        Exam.setAuthor(principal.getAttribute("user_name"));

        return examDao.save(Exam);
    }

    @DeleteMapping("/{examId}")
    public void DeleteExam(@PathVariable UUID examId) throws Exception {
        examDao.deleteById(examId);
    }

    @PutMapping("/{examId}")
    public Exam UpdateExam(@PathVariable UUID examId, @RequestBody Exam Exam) throws Exception {
        if (!Exam.getId().equals(examId))
            throw new Exception("Update error");

        return examDao.save(Exam);
    }

    @GetMapping("/{examId}")
    public Exam GetExam(@PathVariable UUID examId) {
        return examDao.findById(examId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/opened")
    public List<Exam> OpenedExams() {
        return examDao.findByAccessibleFromLessThanEqualAndAccessibleToGreaterThanEqual(Instant.now(),Instant.now());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{examId}/submission")
    public ExamSubmission BeginExam(@PathVariable UUID examId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        var exam = examDao.findById(examId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ExamSubmission examSubmission = new ExamSubmission();
        examSubmission.setAuthor(principal.getAttribute("user_name"));
        examSubmission.setSubmittedAt(Instant.now());
        examSubmission.setExam(exam);
        examSubmission.setExamSubmissionState(ExamSubmissionState.Draft);

        return examSubmissionDao.save(examSubmission);
    }
}
