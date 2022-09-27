package app.stepanek.gamajun.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssignmentInput {
    private UUID id;
    private String title;
    private String description;
    private String xml;
    private String author;
}
