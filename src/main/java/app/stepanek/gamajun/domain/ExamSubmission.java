package app.stepanek.gamajun.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamSubmission extends Submission {


    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_submission_state", nullable = false)
    private ExamSubmissionState examSubmissionState;

    @Column(name = "points", nullable = true)
    private Double points;

    @Column(name = "comment", nullable = true, columnDefinition = "text")
    private String comment;

}
