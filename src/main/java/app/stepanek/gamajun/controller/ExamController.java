package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.graphql.CreateExamInput;
import app.stepanek.gamajun.graphql.UpdateExamInput;
import app.stepanek.gamajun.services.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('GAMAJUN_TEACHER')")
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

    @MutationMapping
    public Exam createExam(@Argument CreateExamInput input) {
        return examService.createExam(input);
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
    public Exam updateExam(@Argument UpdateExamInput input) {
        return examService.update(input);
    }

    @MutationMapping
    public boolean deleteExam(@Argument UUID id) {
        examService.deleteExam(id);
        return true;
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
