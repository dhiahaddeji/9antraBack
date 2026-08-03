package com.esprit.springjwt.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "xp_event")
public class XPEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private XPEventType eventType;

    private Long referenceId;

    private int xpEarned;

    private LocalDateTime timestamp = LocalDateTime.now();

    public XPEvent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public XPEventType getEventType() { return eventType; }
    public void setEventType(XPEventType eventType) { this.eventType = eventType; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
