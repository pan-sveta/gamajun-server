package app.stepanek.gamajun.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpInput {
    private String username;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String inviteCode;
}
