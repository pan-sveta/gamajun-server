package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamSubmissionDao extends JpaRepository<ExamSubmission, UUID> {
    List<ExamSubmission> findByUser_Username(@NonNull String username);
    List<ExamSubmission> findByExam_Id(@NonNull UUID id);
    void deleteExamSubmissionsByUser(User user);

}
