package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.ExamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamSubmissionDao extends JpaRepository<ExamSubmission, UUID> {
    List<ExamSubmission> findByExam_Author(@NonNull String author);
    List<ExamSubmission> findByExam_Id(@NonNull UUID id);

}
