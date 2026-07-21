package com.esprit.springjwt.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activity_logs", indexes = {
    @Index(name = "idx_log_timestamp", columnList = "timestamp"),
    @Index(name = "idx_log_action",    columnList = "action")
})
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW, ERROR */
    @Column(nullable = false, length = 20)
    private String action;

    /** e.g. "User", "Formation", "Chapter", "Group", "Session" */
    @Column(length = 50)
    private String entityType;

    @Column
    private Long entityId;

    @Column(length = 150)
    private String description;

    @Column(length = 100)
    private String username;

    @Column(length = 45)
    private String ipAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp = new Date();

    /** HTTP status code when relevant */
    @Column
    private Integer statusCode;

    public ActivityLog() {}

    public ActivityLog(String action, String entityType, Long entityId,
                       String description, String username, String ipAddress, Integer statusCode) {
        this.action      = action;
        this.entityType  = entityType;
        this.entityId    = entityId;
        this.description = description;
        this.username    = username;
        this.ipAddress   = ipAddress;
        this.statusCode  = statusCode;
        this.timestamp   = new Date();
    }

    // ── getters / setters ─────────────────────────────────────────────────
    public Long getId()                   { return id; }
    public String getAction()             { return action; }
    public void setAction(String a)       { this.action = a; }
    public String getEntityType()         { return entityType; }
    public void setEntityType(String e)   { this.entityType = e; }
    public Long getEntityId()             { return entityId; }
    public void setEntityId(Long e)       { this.entityId = e; }
    public String getDescription()        { return description; }
    public void setDescription(String d)  { this.description = d; }
    public String getUsername()           { return username; }
    public void setUsername(String u)     { this.username = u; }
    public String getIpAddress()          { return ipAddress; }
    public void setIpAddress(String ip)   { this.ipAddress = ip; }
    public Date getTimestamp()            { return timestamp; }
    public void setTimestamp(Date t)      { this.timestamp = t; }
    public Integer getStatusCode()        { return statusCode; }
    public void setStatusCode(Integer s)  { this.statusCode = s; }
}
