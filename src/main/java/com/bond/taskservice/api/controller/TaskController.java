package com.bond.taskservice.api.controller;

import com.bond.taskservice.api.dto.TaskEntity;
import com.bond.taskservice.api.logger.RequestLoggingFilter;
import com.bond.taskservice.api.mapper.TaskMapper;
import com.bond.taskservice.api.model.TaskCreateRequest;
import com.bond.taskservice.api.model.TaskResponse;
import com.bond.taskservice.api.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Batch create (your current style)
    @PostMapping
    public ResponseEntity<List<TaskResponse>> create(@Valid @RequestBody List<TaskCreateRequest> reqs) {
        log.info("Creating requests. Count={}", reqs.size());
        var createdEntities = taskService.createTasks(reqs);
        var responses = createdEntities.stream().map(TaskMapper::toResponse).toList();
        log.info("Created total requests. Count={}", reqs.size());
        return ResponseEntity.status(201).body(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> get(@PathVariable Long id) {
        log.info("Getting task for id - {}", id);
        var entity = taskService.getTask(id);
        log.info("Returned task for id - {}", id);
        return ResponseEntity.ok(TaskMapper.toResponse(entity));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list() {
        var responses = taskService.getTasks().stream().map(TaskMapper::toResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody TaskCreateRequest req) {
        var updated = taskService.updateTask(id, req);
        return ResponseEntity.ok(TaskMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }


}
