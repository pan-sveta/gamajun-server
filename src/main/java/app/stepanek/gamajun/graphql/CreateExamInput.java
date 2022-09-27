package app.stepanek.gamajun.graphql;

import app.stepanek.gamajun.domain.Assignment;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamInput {
    private String title;
    private String author;
    private Instant accessibleFrom;
    private Instant accessibleTo;
    private List<Assignment> assignments = new ArrayList<>();
}
