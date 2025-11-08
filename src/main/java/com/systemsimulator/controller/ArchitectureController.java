package com.systemsimulator.controller;

import com.systemsimulator.model.*;
import com.systemsimulator.service.ArchitectureService;
import com.systemsimulator.service.RuleEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/architecture")
@CrossOrigin(origins = "*")
public class ArchitectureController {

    @Autowired
    private ArchitectureService architectureService;

    @Autowired
    private RuleEngineService ruleEngineService;

    // ==================== CRUD OPERATIONS ====================

    /** Get all architectures */
    @GetMapping
    public ResponseEntity<List<Architecture>> getAllArchitectures() {
        return ResponseEntity.ok(architectureService.getAllArchitectures());
    }

    /** Get architecture by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Architecture> getArchitectureById(@PathVariable String id) {
        Optional<Architecture> architecture = architectureService.getArchitectureById(id);
        return architecture.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Create new architecture */
    @PostMapping
    public ResponseEntity<Architecture> createArchitecture(@RequestBody ArchitectureRequest request) {
        Architecture architecture = architectureService.createArchitecture(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(architecture);
    }

    /** Delete architecture */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArchitecture(@PathVariable String id) {
        boolean deleted = architectureService.deleteArchitecture(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ==================== COMPONENT / LINK MANAGEMENT ====================

    @PostMapping("/{id}/components")
    public ResponseEntity<?> addComponentToArchitecture(
            @PathVariable String id,
            @RequestBody ComponentReference componentRef) {
        try {
            Architecture updated = architectureService.addComponentToArchitectureById(id, componentRef.getComponentId());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<?> addLinkToArchitecture(
            @PathVariable String id,
            @RequestBody LinkReference linkRef) {
        try {
            Architecture updated = architectureService.addLinkToArchitectureById(id, linkRef.getLinkId());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==================== EVALUATION / VISUALIZATION ====================

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateArchitecture(@RequestBody EvaluationRequest request) {
        try {
            ArchitectureService.ArchitectureEvaluation evaluation =
                    architectureService.evaluateArchitectureDetailed(request.getArchitectureId());
            return ResponseEntity.ok(evaluation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}/score")
    public ResponseEntity<?> getArchitectureScore(@PathVariable String id) {
        try {
            double score = architectureService.evaluateArchitecture(id);
            return ResponseEntity.ok(new ScoreResponse(id, score));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/visualize/{id}")
    public ResponseEntity<?> visualizeArchitecture(@PathVariable String id) {
        return architectureService.getArchitectureById(id)
                .map(arch -> {
                    VisualizationData data = new VisualizationData();
                    data.setArchitectureId(arch.getId());
                    data.setArchitectureName(arch.getName());
                    data.setComponents(arch.getComponents());
                    data.setLinks(arch.getLinks());
                    return ResponseEntity.ok((Object) data);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Architecture not found: " + id)));
    }

    // ==================== COMPARISON / VALIDATION ====================

    @PostMapping("/compare")
    public ResponseEntity<?> compareArchitectures(@RequestBody ComparisonRequest request) {
        try {
            ArchitectureService.ArchitectureComparison comparison =
                    architectureService.compareArchitectures(
                            request.getArchitecture1Id(),
                            request.getArchitecture2Id()
                    );
            return ResponseEntity.ok(comparison);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<?> validateArchitecture(@PathVariable String id) {
        return architectureService.getArchitectureById(id)
                .map(arch -> {
                    RuleEngineService.ArchitectureValidationResult validation =
                            ruleEngineService.validateArchitecture(arch);
                    return ResponseEntity.ok((Object) validation);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Architecture not found: " + id)));
    }

    // ==================== RULES ====================

    @GetMapping("/rules")
    public ResponseEntity<List<ConnectionRule>> getAllRules() {
        return ResponseEntity.ok(ruleEngineService.getAllRules());
    }

    @GetMapping("/rules/{linkType}")
    public ResponseEntity<List<ConnectionRule>> getRulesForLinkType(@PathVariable LinkType linkType) {
        return ResponseEntity.ok(ruleEngineService.getRulesForLinkType(linkType));
    }

    // ==================== DTO CLASSES ====================

    public static class ArchitectureRequest {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class EvaluationRequest {
        private String architectureId;
        public String getArchitectureId() { return architectureId; }
        public void setArchitectureId(String architectureId) { this.architectureId = architectureId; }
    }

    public static class ComparisonRequest {
        private String architecture1Id;
        private String architecture2Id;
        public String getArchitecture1Id() { return architecture1Id; }
        public void setArchitecture1Id(String architecture1Id) { this.architecture1Id = architecture1Id; }
        public String getArchitecture2Id() { return architecture2Id; }
        public void setArchitecture2Id(String architecture2Id) { this.architecture2Id = architecture2Id; }
    }

    public static class ScoreResponse {
        private String architectureId;
        private double score;
        public ScoreResponse(String architectureId, double score) {
            this.architectureId = architectureId;
            this.score = score;
        }
        public String getArchitectureId() { return architectureId; }
        public double getScore() { return score; }
    }

    public static class VisualizationData {
        private String architectureId;
        private String architectureName;
        private List<Component> components;
        private List<Link> links;
        public String getArchitectureId() { return architectureId; }
        public void setArchitectureId(String architectureId) { this.architectureId = architectureId; }
        public String getArchitectureName() { return architectureName; }
        public void setArchitectureName(String architectureName) { this.architectureName = architectureName; }
        public List<Component> getComponents() { return components; }
        public void setComponents(List<Component> components) { this.components = components; }
        public List<Link> getLinks() { return links; }
        public void setLinks(List<Link> links) { this.links = links; }
    }

    public static class ErrorResponse {
        private String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }

    public static class ComponentReference {
        private String componentId;
        public String getComponentId() { return componentId; }
        public void setComponentId(String componentId) { this.componentId = componentId; }
    }

    public static class LinkReference {
        private String linkId;
        public String getLinkId() { return linkId; }
        public void setLinkId(String linkId) { this.linkId = linkId; }
    }
}
