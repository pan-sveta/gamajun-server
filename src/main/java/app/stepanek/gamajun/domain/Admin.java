package app.stepanek.gamajun.domain;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

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
