package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.dto.StudentExamDTO;
import app.stepanek.gamajun.dto.StudentExamSubmissionDTO;
import app.stepanek.gamajun.graphql.CreateExamInput;
import app.stepanek.gamajun.graphql.UpdateExamInput;
import app.stepanek.gamajun.repository.ExamDao;
import app.stepanek.gamajun.services.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
public class ExamController {
    private final ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @SchemaMapping
    public Exam exam(Exam exam) {
        return examService.findById(exam.getId());
    }

    public Exam creatExam(@Argument CreateExamInput createExamInput) {
        return examService.createExam(createExamInput);
    }

    @QueryMapping
    public List<Exam> exams() {
        return examService.findAll();
    }

    @QueryMapping
    public Exam examById(@Argument UUID id) {
        return examService.findById(id);
    }

    @MutationMapping
    public Exam updateExam(@Argument UpdateExamInput updateExamInput) throws Exception {
        return examService.update(updateExamInput);
    }

    @MutationMapping
    public void deleteExam(@Argument UUID id) {
        examService.deleteExam(id);
    }


    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<Exam> openedExams() {
        return examService.getOpenedExams();
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public ExamSubmission beginExam(@Argument UUID id) {
        return examService.beginExam(id);
    }
}
