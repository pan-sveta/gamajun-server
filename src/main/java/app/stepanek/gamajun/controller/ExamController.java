package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.repository.ExamDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exams")
@PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
public class ExamController {
    private final ExamDao ExamDao;

    @Autowired
    public ExamController(ExamDao ExamDao) {
        this.ExamDao = ExamDao;
    }

    @GetMapping
    public List<Exam> AllExams() {
        return ExamDao.findAll();
    }

    @PostMapping
    public Exam CreatExam(@RequestBody Exam Exam, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        Exam.setAuthor(principal.getAttribute("user_name"));

        return ExamDao.save(Exam);
    }

    @DeleteMapping("/{examId}")
    public void DeleteExam(@PathVariable UUID examId) throws Exception {
        ExamDao.deleteById(examId);
    }

    @PutMapping("/{examId}")
    public Exam UpdateExam(@PathVariable UUID examId, @RequestBody Exam Exam) throws Exception {
        if (!Exam.getId().equals(examId))
            throw new Exception("Update error");

        return ExamDao.save(Exam);
    }

    @GetMapping("/{examId}")
    public Exam GetExam(@PathVariable UUID examId) {
        return ExamDao.findById(examId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
