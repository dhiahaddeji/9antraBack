package com.esprit.springjwt.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_badge")
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String badgeKey;
    private String badgeName;
    private String category;
    private int level;
    private String icon;

    private LocalDateTime earnedAt = LocalDateTime.now();

    public UserBadge() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getBadgeKey() { return badgeKey; }
    public void setBadgeKey(String badgeKey) { this.badgeKey = badgeKey; }

    public String getBadgeName() { return badgeName; }
    public void setBadgeName(String badgeName) { this.badgeName = badgeName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public LocalDateTime getEarnedAt() { return earnedAt; }
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; }
}
