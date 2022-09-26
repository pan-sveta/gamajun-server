package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.domain.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExamDao extends JpaRepository<Exam, UUID> {
    List<Exam> findByAccessibleFromLessThanEqualAndAccessibleToGreaterThanEqual(@NonNull Instant accessibleFrom, @NonNull Instant accessibleTo);


}
