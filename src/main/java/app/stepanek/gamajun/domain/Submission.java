package app.stepanek.gamajun.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public class Submission {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    protected UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    protected Assignment assignment;

    @Column(name = "xml", nullable = true, columnDefinition = "text")
    protected String xml;

    @Column(name = "started_at", nullable = false)
    protected Instant startedAt;

    @Column(name = "submitted_at", nullable = true)
    protected Instant submittedAt;

    @Column(name = "author", nullable = false)
    protected String author;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "validator_report_id")
    protected ValidatorReport validatorReport;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Submission that)) return false;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
