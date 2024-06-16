package com.vts.vaccation_tracking_system.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "clerk_verification_token")
public class ClerkVerificationToken extends VerificationToken{
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
    @JoinColumn(name = "clerk_id", nullable = false)
    private HRClerk clerk;

    public AbstractUser getUser() {
        return clerk;
    }

    public void setClerk(HRClerk clerk) {
        this.clerk = clerk;
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