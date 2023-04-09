package app.stepanek.gamajun.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {
    @Id
    private UUID id;
    private String name;
    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "classroom_users",
            joinColumns = @JoinColumn(name = "classroom_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "classroom_user", referencedColumnName = "username"))
    private Set<User> users;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "classrooms")
    private Set<Exam> exams = new HashSet<>();

    @PreRemove
    private void removeAssociations() {
        for (Exam exam : exams) {
            exam.getClassrooms().remove(this);
        }
    }
}
