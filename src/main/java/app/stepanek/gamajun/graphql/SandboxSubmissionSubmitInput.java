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
public class SandboxSubmissionSubmitInput {
    private UUID id;
    private String xml;
}
