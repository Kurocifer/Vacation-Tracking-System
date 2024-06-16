package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.ClerkVerificationToken;
import com.vts.vaccation_tracking_system.model.HRClerk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClerkVerificationTokenDAO extends JpaRepository<ClerkVerificationToken, Long> {
    Optional<ClerkVerificationToken> findByToken(String token);

    void deleteByClerk(HRClerk clerk);
}
