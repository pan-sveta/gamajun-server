package app.stepanek.gamajun.repository;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassroomDao extends JpaRepository<Classroom, UUID> {
    Optional<Classroom> findClassroomByInviteCode(String inviteCode);
    Optional<Classroom> findClassroomByUsersContains(User user);
}
