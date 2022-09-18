package app.stepanek.gamajun.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Type(type = "text")
    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Lob
    @Type(type = "text")
    @Column(name = "xml", nullable = false, columnDefinition = "text")
    private String xml;

    @Column(name = "author", nullable = false)
    private String author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Assignment that = (Assignment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
