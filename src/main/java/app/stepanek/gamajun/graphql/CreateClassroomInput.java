package app.stepanek.gamajun.graphql;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateClassroomInput {
    private String name;
    private String inviteCode;
}
