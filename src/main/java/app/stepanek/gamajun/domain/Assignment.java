package app.stepanek.gamajun.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "xml", nullable = false, columnDefinition = "text")
    private String xml;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_username", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    @Column(name = "sandbox", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean sandbox;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment that)) return false;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
