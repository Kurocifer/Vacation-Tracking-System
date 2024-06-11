package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.HRClerk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClerkDAO extends JpaRepository<HRClerk, Long> {
}
