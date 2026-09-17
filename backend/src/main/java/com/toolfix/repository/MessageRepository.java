package com.toolfix.repository;

import com.toolfix.domain.DiagnosisSession;
import com.toolfix.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySessionOrderByCreatedAtAsc(DiagnosisSession session);
    List<Message> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
