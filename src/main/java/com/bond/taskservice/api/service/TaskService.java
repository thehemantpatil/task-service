package com.bond.taskservice.api.service;

import com.bond.taskservice.api.controller.TaskController;
import com.bond.taskservice.api.dto.TaskEntity;
import com.bond.taskservice.api.exception.TaskNotFoundException;
import com.bond.taskservice.api.mapper.TaskMapper;
import com.bond.taskservice.api.model.TaskCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final AtomicLong idGenerator = new AtomicLong(1000);
    private final Map<Long, TaskEntity> store = new ConcurrentHashMap<>();

    // Batch create (matches your earlier design)
    public List<TaskEntity> createTasks(List<TaskCreateRequest> requests) {
        List<TaskEntity> created = new ArrayList<>(requests.size());
        for (TaskCreateRequest req : requests) {
            created.add(createTask(req));
        }
        return created;
    }

    // Single create helper (useful if you switch POST to single request later)
    public TaskEntity createTask(TaskCreateRequest req) {
        long id = idGenerator.incrementAndGet();
        LocalDateTime now = LocalDateTime.now();

        TaskEntity entity = TaskMapper.toNewEntity(req, id, now);
        System.out.println("Created task : " + entity.toString());
        store.put(id, entity);
        return entity;
    }

    public TaskEntity getTask(Long id) {
        log.info("Fetching request for id - {} ", id);
        TaskEntity entity = store.get(id);
        if (entity == null) {
            log.info("Task not found for id - {}", id);
            throw new TaskNotFoundException(id);
        }
        log.info("Fetching task details for id - {} ", id);
        return entity;
    }

    public List<TaskEntity> getTasks() {
        return new ArrayList<>(store.values());
    }

    // PUT semantics: replace updatable fields
    public TaskEntity updateTask(Long id, TaskCreateRequest req) {
        TaskEntity entity = store.get(id);
        if (entity == null) throw new TaskNotFoundException(id);

        TaskMapper.applyUpdate(entity, req, LocalDateTime.now());
        store.put(id, entity);
        return entity;
    }

    public void deleteTask(Long id) {
        TaskEntity removed = store.remove(id);
        if (removed == null) throw new TaskNotFoundException(id);
    }
}