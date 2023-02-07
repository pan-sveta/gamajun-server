package app.stepanek.gamajun.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Admin {
    public Admin(String username) {
        this.username = username;
    }

    @Id
    @Column(name = "username", nullable = false)
    private String username;
}
