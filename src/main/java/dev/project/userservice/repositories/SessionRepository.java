package dev.project.userservice.repositories;

import dev.project.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByTokenAndUser_Id(String token, Long userId);

    // delete all sessions for a given user id
    void deleteAllByUser_Id(Long userId);

    // optional: find all sessions for a user
    List<Session> findAllByUser_Id(Long userId);
}
