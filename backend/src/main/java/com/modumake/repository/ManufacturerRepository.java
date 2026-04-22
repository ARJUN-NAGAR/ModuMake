package com.modumake.repository;

import com.modumake.model.Manufacturer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturerRepository extends MongoRepository<Manufacturer, String> {
    List<Manufacturer> findByTier(Manufacturer.Tier tier);
}
