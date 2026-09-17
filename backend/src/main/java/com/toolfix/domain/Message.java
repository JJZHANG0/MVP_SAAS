package com.toolfix.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "messages")
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private DiagnosisSession session;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String imageUrls;
    
    @Column(nullable = false)
    private Integer roundNumber;
    
    private Boolean isHazardWarning = false;
    
    private Boolean isFromHuman = false;
    
    public enum MessageRole {
        USER,
        ASSISTANT,
        SYSTEM
    }
}
