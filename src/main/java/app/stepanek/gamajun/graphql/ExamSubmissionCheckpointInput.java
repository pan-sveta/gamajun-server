package app.stepanek.gamajun.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubmissionCheckpointInput {
    private UUID id;
    private String xml;
}
