package app.stepanek.gamajun.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceMatchingResult {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private ReferenceMatchingResultState result;

    @Column(name = "isomorphism_result", nullable = false)
    private boolean isomorphismCheckResult;

    @Column(name = "participant_count_result", nullable = false)
    private boolean participantsCheckResult;

    @Column(name = "isomorphism_message")
    private String participantsCheckMessage;
}
