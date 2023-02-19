package app.stepanek.gamajun.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_username", nullable = false)
    private User author;

    @Column(name = "accessible_from", nullable = false)
    private Instant accessibleFrom;

    @Column(name = "accessible_to", nullable = false)
    private Instant accessibleTo;

    @Column(name = "time_limit", nullable = false)
    //In minutes
    private int timeLimit;

    @ManyToMany
    @JoinTable(name = "exam_assignments",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "assignments_id"))
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "exam_classrooms",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "classroom_id"))
    private List<Classroom> classrooms = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exam exam)) return false;

        return Objects.equals(id, exam.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
