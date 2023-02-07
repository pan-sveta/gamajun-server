package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.ValidatorRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidatorRuleDao extends JpaRepository<ValidatorRule, String> {

}
