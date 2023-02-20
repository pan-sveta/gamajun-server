package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.ValidatorReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ValidatorReportDao extends JpaRepository<ValidatorReport, UUID> {
}
