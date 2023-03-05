package app.stepanek.gamajun.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExamInput {
    private String title;
    private Instant accessibleFrom;
    private Instant accessibleTo;
    private int timeLimit;
    private List<UUID> assignmentIds = new ArrayList<>();
    private List<UUID> classroomIds = new ArrayList<>();
}
