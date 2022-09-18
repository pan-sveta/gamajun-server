package app.stepanek.gamajun.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
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

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "accessible_from", nullable = false)
    private Instant accessibleFrom;

    @Column(name = "accessible_to", nullable = false)
    private Instant accessibleTo;

    @ManyToMany
    @JoinTable(name = "exam_assignments",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "assignments_id"))
    private List<Assignment> assignments = new ArrayList<>();

}
