package app.stepanek.gamajun.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected User user;

    @OneToOne
    @Cascade(CascadeType.ALL)
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
