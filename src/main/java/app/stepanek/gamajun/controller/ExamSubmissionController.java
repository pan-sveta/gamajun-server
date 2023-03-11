package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.graphql.ExamSubmissionCheckpointInput;
import app.stepanek.gamajun.graphql.ExamSubmissionSubmitInput;
import app.stepanek.gamajun.services.ExamSubmissionService;
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
public class ExamSubmissionController {
    private final ExamSubmissionService examSubmissionService;

    @Autowired
    public ExamSubmissionController(ExamSubmissionService examSubmissionService) {
        this.examSubmissionService = examSubmissionService;
    }

    @SchemaMapping
    public ExamSubmission examSubmission(ExamSubmission examSubmission) {
        return examSubmissionService.findById(examSubmission.getId());
    }

    @Secured("GAMAJUN_TEACHER")
    @QueryMapping
    public List<ExamSubmission> examSubmissions() {
        return examSubmissionService.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<ExamSubmission> examSubmissionsByExamId(@Argument UUID examId) {
        return examSubmissionService.findAllByExam(examId);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public ExamSubmission examSubmissionById(@Argument UUID id) {
        return examSubmissionService.findById(id);
    }

    @Secured("GAMAJUN_TEACHER")
    @MutationMapping
    public boolean deleteExamSubmission(@PathVariable UUID examSubmissionId) {
        return examSubmissionService.delete(examSubmissionId);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<ExamSubmission> myExamSubmissions() {
        return examSubmissionService.mySubmissions();
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public ExamSubmission checkpointExamSubmission(@Argument ExamSubmissionCheckpointInput input) {
        return examSubmissionService.checkpointStudentExam(input);
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public ExamSubmission submitExamSubmission(@Argument ExamSubmissionSubmitInput input)  {
        return examSubmissionService.submitStudentExam(input);
    }

}
