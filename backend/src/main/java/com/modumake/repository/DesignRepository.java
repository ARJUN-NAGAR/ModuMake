package com.modumake.repository;

import com.modumake.model.Design;
import com.modumake.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignRepository extends MongoRepository<Design, String> {
    List<Design> findByUser(User user);
    List<Design> findByStatus(Design.Status status);
}
