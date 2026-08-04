package com.esprit.springjwt.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_xp")
public class UserXP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private int totalXp = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "xp_rank")
    private XPRank rank = XPRank.BRONZE;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    public UserXP() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public XPRank getRank() { return rank; }
    public void setRank(XPRank rank) { this.rank = rank; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
