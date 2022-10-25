package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.SandboxSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SandboxSubmissionDao extends JpaRepository<SandboxSubmission, UUID> {
    List<SandboxSubmission> findByAssignment_Id(UUID id);

    List<SandboxSubmission> findByAuthor(String author);
}
