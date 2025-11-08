package com.systemsimulator.repository;

import com.systemsimulator.model.Architecture;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchitectureRepository extends MongoRepository<Architecture, String> {
    // Add derived query methods here if you need them later, e.g.:
    // Optional<Architecture> findByName(String name);
}
