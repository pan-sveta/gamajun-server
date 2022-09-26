package app.stepanek.gamajun.dto;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
public class StudentExamSubmissionDTO {
    private UUID id;
    private StudentExamDTO exam;
    private StudentAssignmentDTO assignment;
    private String xml;
    private Instant startedAt;
    private Instant submittedAt;
    private String author;
    private ExamSubmissionState examSubmissionState;

}
