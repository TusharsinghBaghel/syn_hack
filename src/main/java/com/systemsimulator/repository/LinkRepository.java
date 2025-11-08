package com.systemsimulator.repository;

import com.systemsimulator.model.Link;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends MongoRepository<Link, String> {

    // Custom query methods for MongoDB

    List<Link> findBySource_Id(String sourceId);

    List<Link> findByTarget_Id(String targetId);
}
