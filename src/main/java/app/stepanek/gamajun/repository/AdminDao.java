package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDao extends JpaRepository<Admin, String> {
    @Query("select (count(a) > 0) from Admin a where a.username = ?1")
    boolean existsByUsername(String username);

}
