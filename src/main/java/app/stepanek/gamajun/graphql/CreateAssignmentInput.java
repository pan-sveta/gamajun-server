package app.stepanek.gamajun.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAssignmentInput {
    private String title;
    private String description;
    private String xml;
    private boolean sandbox;
}

