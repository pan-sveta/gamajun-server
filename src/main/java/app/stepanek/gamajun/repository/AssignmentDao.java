package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssignmentDao extends JpaRepository<Assignment, UUID> {

}
