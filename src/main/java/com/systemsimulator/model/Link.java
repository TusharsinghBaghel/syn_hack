package com.systemsimulator.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Document(collection = "links")
public class Link {

    @Id
    private String id;

    // ⚙️ Reference components — choose between embedded or referenced storage
    @DBRef(lazy = true)
    private Component source;

    @DBRef(lazy = true)
    private Component target;

    private LinkType type;
    private HeuristicProfile heuristics = new HeuristicProfile();
    private Map<String, Object> properties = new HashMap<>();

    public Link() {}

    public Link(String id, Component source, Component target, LinkType type) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
    }
}
