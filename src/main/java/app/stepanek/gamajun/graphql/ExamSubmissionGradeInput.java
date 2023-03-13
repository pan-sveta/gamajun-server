package app.stepanek.gamajun.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSubmissionGradeInput {
    private UUID id;
    private double points;
    private String comment;
}
