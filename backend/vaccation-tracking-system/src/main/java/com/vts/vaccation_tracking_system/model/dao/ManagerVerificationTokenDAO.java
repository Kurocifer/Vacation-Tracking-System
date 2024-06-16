package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Manager;
import com.vts.vaccation_tracking_system.model.ManagerVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerVerificationTokenDAO extends JpaRepository<ManagerVerificationToken, Long> {
    Optional<ManagerVerificationToken> findByToken(String token);

    void deleteByManager(Manager manager);
}
