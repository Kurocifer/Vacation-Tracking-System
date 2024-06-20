package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RequestDAO extends JpaRepository<Request, Long> {

//    @Override
//    Optional<Request> findById(Long aLong);

    Optional<Request> findByEmployee_Username(String username);

    Optional<Request> findByManager_Username(String username);

    boolean existsByEmployee_UsernameIgnoreCase(String username);

    boolean existsByManager_UsernameIgnoreCase(String username);


    @Transactional
    @Modifying
    @Query("update Request r set r.isValidated = ?1 where r.id = ?2")
    int updateIsValidatedById(Boolean isValidated, Long id);
}
