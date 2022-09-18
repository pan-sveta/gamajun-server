package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.domain.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamDao extends JpaRepository<Exam, UUID> {

}
