package com.systemsimulator.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Document(collection = "components")
public abstract class Component {

    @Id
    private String id;

    private String name;

    private HeuristicProfile heuristics = new HeuristicProfile();
    private Map<String, Object> properties = new HashMap<>();

    public Component() {}

    public Component(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Each concrete subclass must define its type
    public abstract ComponentType getType();
}
