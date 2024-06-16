package com.vts.vaccation_tracking_system.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "manager_verification_token")
public class ManagerVerificationToken extends VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_time_stamp", nullable = false)
    private Timestamp createdTimeStamp;

    @Lob
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    public AbstractUser gerUser() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
    public AbstractUser getUser() {
        return manager;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}