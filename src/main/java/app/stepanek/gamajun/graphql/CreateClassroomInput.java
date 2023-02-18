package app.stepanek.gamajun.graphql;

import lombok.Data;

@Data
public class CreateClassroomInput {
    private String name;
    private String inviteCode;
}
