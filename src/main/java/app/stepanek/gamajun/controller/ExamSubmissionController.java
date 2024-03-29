package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.graphql.ExamSubmissionCheckpointInput;
import app.stepanek.gamajun.graphql.ExamSubmissionGradeInput;
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

    @Secured("GAMAJUN_TEACHER")
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
    public boolean deleteExamSubmission(@Argument UUID examSubmissionId) {
        examSubmissionService.delete(examSubmissionId);
        return true;
    }

    @Secured("GAMAJUN_TEACHER")
    @MutationMapping
    public ExamSubmission gradeExamSubmission(@Argument ExamSubmissionGradeInput input) {
        return examSubmissionService.gradeStudentExam(input);
    }

    @Secured("GAMAJUN_STUDENT")
    @QueryMapping
    public List<ExamSubmission> myExamSubmissions() {
        return examSubmissionService.mySubmissions();
    }

    @Secured("GAMAJUN_STUDENT")
    @MutationMapping
    public ExamSubmission checkpointExamSubmission(@Argument ExamSubmissionCheckpointInput input) {
        return examSubmissionService.checkpointStudentExam(input);
    }

    @Secured("GAMAJUN_STUDENT")
    @MutationMapping
    public ExamSubmission submitExamSubmission(@Argument ExamSubmissionSubmitInput input)  {
        return examSubmissionService.submitStudentExam(input);
    }

}
