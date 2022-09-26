package app.stepanek.gamajun.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class StudentExamDTO {
    private UUID id;
    private String title;
    private Instant accessibleFrom;
    private Instant accessibleTo;
}
