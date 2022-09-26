package app.stepanek.gamajun.dto;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Data
public class StudentAssignmentDTO {
    private UUID id;
    private String title;
    private String description;
}
