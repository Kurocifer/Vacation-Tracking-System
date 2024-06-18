package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestDAO extends JpaRepository<Request, Long> {
}
