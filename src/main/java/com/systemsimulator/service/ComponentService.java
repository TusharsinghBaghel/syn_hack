package com.systemsimulator.service;

import com.systemsimulator.model.*;
import com.systemsimulator.repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComponentService {

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private HeuristicService heuristicService;

    /**
     * Create and save a new component of a given type.
     */
    public Component createComponent(ComponentType type, String id, String name, Map<String, Object> properties) {
        // Generate an ID if none is provided
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }

        Component component = instantiateComponent(type, id, name, properties);
        component.setProperties(properties);

        // Initialize heuristics based on component type and subtype
        HeuristicProfile heuristics = heuristicService.getHeuristicsForComponent(component);
        component.setHeuristics(heuristics);

        // Persist in MongoDB
        return componentRepository.save(component);
    }

    /**
     * Save or update a component.
     */
    public Component saveComponent(Component component) {
        return componentRepository.save(component);
    }

    /**
     * Find a component by ID.
     */
    public Optional<Component> getComponentById(String id) {
        return componentRepository.findById(id);
    }

    /**
     * Retrieve all components.
     */
    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    /**
     * Delete component by ID.
     */
    public boolean deleteComponent(String id) {
        if (!componentRepository.existsById(id)) {
            return false;
        }
        componentRepository.deleteById(id);
        return true;
    }

    /**
     * Check if a component exists by ID.
     */
    public boolean componentExists(String id) {
        return componentRepository.existsById(id);
    }

    /**
     * Factory method to instantiate the right component subclass
     * based on type and subtype.
     */
    private Component instantiateComponent(ComponentType type, String id, String name, Map<String, Object> properties) {
        switch (type) {
            case DATABASE:
                DatabaseComponent.DatabaseType dbType = DatabaseComponent.DatabaseType.SQL;
                if (properties.containsKey("subtype")) {
                    try {
                        dbType = DatabaseComponent.DatabaseType.valueOf(properties.get("subtype").toString());
                    } catch (IllegalArgumentException ignored) {}
                }
                return new DatabaseComponent(id, name, dbType);

            case CACHE:
                CacheComponent.CacheType cacheType = CacheComponent.CacheType.IN_MEMORY;
                if (properties.containsKey("subtype")) {
                    try {
                        cacheType = CacheComponent.CacheType.valueOf(properties.get("subtype").toString());
                    } catch (IllegalArgumentException ignored) {}
                }
                return new CacheComponent(id, name, cacheType);

            case API_SERVICE:
                APIServiceComponent.APIType apiType = APIServiceComponent.APIType.REST;
                if (properties.containsKey("subtype")) {
                    try {
                        apiType = APIServiceComponent.APIType.valueOf(properties.get("subtype").toString());
                    } catch (IllegalArgumentException ignored) {}
                }
                return new APIServiceComponent(id, name, apiType);

            case QUEUE:
                QueueComponent.QueueType queueType = QueueComponent.QueueType.MESSAGE_QUEUE;
                if (properties.containsKey("subtype")) {
                    try {
                        queueType = QueueComponent.QueueType.valueOf(properties.get("subtype").toString());
                    } catch (IllegalArgumentException ignored) {}
                }
                return new QueueComponent(id, name, queueType);

            case STORAGE:
                StorageComponent.StorageType storageType = StorageComponent.StorageType.OBJECT_STORAGE;
                if (properties.containsKey("subtype")) {
                    try {
                        storageType = StorageComponent.StorageType.valueOf(properties.get("subtype").toString());
                    } catch (IllegalArgumentException ignored) {}
                }
                return new StorageComponent(id, name, storageType);

            case LOAD_BALANCER:
                LoadBalancerComponent.LoadBalancerType lbType = LoadBalancerComponent.LoadBalancerType.ROUND_ROBIN;
                if (properties.containsKey("subtype")) {
                    try {
                        lbType = LoadBalancerComponent.LoadBalancerType.valueOf(properties.get("subtype").toString());
                    } catch (IllegalArgumentException ignored) {}
                }
                return new LoadBalancerComponent(id, name, lbType);

            case CLIENT:
                return new ClientComponent(id, name);

            case STREAM_PROCESSOR:
                return new StreamProcessorComponent(id, name);

            case BATCH_PROCESSOR:
                return new BatchProcessorComponent(id, name);

            case EXTERNAL_SERVICE:
                return new ExternalServiceComponent(id, name);

            default:
                throw new IllegalArgumentException("Unsupported component type: " + type);
        }
    }
}
