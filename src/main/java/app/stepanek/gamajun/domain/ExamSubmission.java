package app.stepanek.gamajun.domain;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ExamSubmission extends Submission {
    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_submission_state", nullable = false)
    private ExamSubmissionState examSubmissionState;
}
