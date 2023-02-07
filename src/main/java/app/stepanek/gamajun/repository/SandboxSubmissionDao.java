package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.SandboxSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SandboxSubmissionDao extends JpaRepository<SandboxSubmission, UUID> {
    List<SandboxSubmission> findByAssignment_Id(UUID id);

    List<SandboxSubmission> findByAssignment_IdAndAuthor(UUID id, String author);



}
