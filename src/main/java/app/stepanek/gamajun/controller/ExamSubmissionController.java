package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.command.ExamSubmissionCheckpointCommand;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.command.ExamSubmissionSubmitCommand;
import app.stepanek.gamajun.dto.StudentExamSubmissionDTO;
import app.stepanek.gamajun.graphql.ExamSubmissionCheckpointInput;
import app.stepanek.gamajun.graphql.ExamSubmissionSubmitInput;
import app.stepanek.gamajun.services.ExamSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
    @QueryMapping
    public List<ExamSubmission> examSubmissions() {
        return examSubmissionService.findAll();
    }

    @QueryMapping
    public ExamSubmission examSubmissionById(@Argument UUID id) {
        return examSubmissionService.findById(id);
    }

    @PreAuthorize("hasRole('ROLE_GAMAJUN_ADMIN')")
    @MutationMapping
    public void deleteExamSubmission(@PathVariable UUID examSubmissionId) {
        examSubmissionService.delete(examSubmissionId);
    }

    @QueryMapping
    public List<StudentExamSubmissionDTO> myExamSubmissions() {
        return examSubmissionService.mySubmissions();
    }

    @MutationMapping
    public ExamSubmission checkpointExamSubmission(@Argument ExamSubmissionCheckpointInput examSubmissionCheckpointInput) {
        return examSubmissionService.checkpointStudentExam(examSubmissionCheckpointInput);
    }

    @MutationMapping
    public ExamSubmission submitExamSubmission(@Argument ExamSubmissionSubmitInput examSubmissionSubmitInput)  {
        return examSubmissionService.submitStudentExam(examSubmissionSubmitInput);
    }

}
