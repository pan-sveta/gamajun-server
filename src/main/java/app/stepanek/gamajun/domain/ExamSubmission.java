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
public class ExamSubmission {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(name = "xml", nullable = true, columnDefinition = "text")
    private String xml;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "submitted_at", nullable = true)
    private Instant submittedAt;

    @Column(name = "author", nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_submission_state", nullable = false)
    private ExamSubmissionState examSubmissionState;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "validator_report_id")
    private ValidatorReport validatorReport;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExamSubmission that)) return false;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
