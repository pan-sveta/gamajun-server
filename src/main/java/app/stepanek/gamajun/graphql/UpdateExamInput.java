package app.stepanek.gamajun.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExamInput {
    private UUID id;
    private String title;
    private Instant accessibleFrom;
    private Instant accessibleTo;
    private int timeLimit;
    private List<UUID> assignmentIds = new ArrayList<>();
}
