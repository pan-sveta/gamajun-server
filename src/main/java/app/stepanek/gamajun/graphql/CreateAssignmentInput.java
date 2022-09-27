package app.stepanek.gamajun.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssignmentInput {
    private String title;
    private String description;
    private String xml;
    private String author;
}

