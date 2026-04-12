package com.bond.taskservice.api.mapper;

import com.bond.taskservice.api.dto.TaskEntity;
import com.bond.taskservice.api.model.TaskCreateRequest;
import com.bond.taskservice.api.model.TaskResponse;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


public final class TaskMapper {

    private TaskMapper() {
    }

    // Create: Request -> new Entity
    public static TaskEntity toNewEntity(TaskCreateRequest req, Long id, LocalDateTime now) {
        TaskEntity e = new TaskEntity();
        e.setId(id);
        e.setTaskName(req.getTaskName());
        e.setPriority(req.getPriority());
        e.setCreatedAt(now);
        e.setUpdatedAt(now);
        return e;
    }

    // Update: apply changes on existing entity
    public static void applyUpdate(TaskEntity existing, TaskCreateRequest req, LocalDateTime now) {
        existing.setTaskName(req.getTaskName());
        existing.setPriority(req.getPriority());
        existing.setUpdatedAt(now);
    }

    // Entity -> Response
    public static TaskResponse toResponse(TaskEntity e) {
        return new TaskResponse(
                e.getId(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDueDate(),
                e.getTaskName(),
                e.getUpdatedBy(),
                e.getCreatedBy(),
                e.getPriority()
        );
    }
}
