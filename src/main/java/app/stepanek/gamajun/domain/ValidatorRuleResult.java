package app.stepanek.gamajun.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidatorRuleResult {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "validator_rule_id", nullable = false)
    private ValidatorRule validatorRule;

    @Column(name = "valid", nullable = false)
    private Boolean valid = false;

    @Column(name = "message", length = 1024)
    private String message;

}
