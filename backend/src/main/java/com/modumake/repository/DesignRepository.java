package com.modumake.repository;

import com.modumake.model.Design;
import com.modumake.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DesignRepository extends JpaRepository<Design, UUID> {
    List<Design> findByUser(User user);
    List<Design> findByStatus(Design.Status status);
}
