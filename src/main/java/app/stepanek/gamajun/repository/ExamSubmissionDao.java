package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.ExamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamSubmissionDao extends JpaRepository<ExamSubmission, UUID> {

}
