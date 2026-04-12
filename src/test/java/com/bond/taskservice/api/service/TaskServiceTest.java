package com.bond.taskservice.api.service;

import com.bond.taskservice.api.exception.TaskNotFoundException;
import com.bond.taskservice.api.model.TaskCreateRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {


    @Test
    void getTask_shouldThrowNotFound_whenMissing() {
        TaskService service = new TaskService();
        assertThrows(TaskNotFoundException.class, () -> service.getTask(999L));
    }

    @Test
    void createTask_shouldAssignIdAndTimestamps() {
        TaskService service = new TaskService();

        TaskCreateRequest req = new TaskCreateRequest();
        req.setTaskName("Test");
        req.setPriority(3);

        var entity = service.createTask(req);

        assertNotNull(entity.getId());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals("Test", entity.getTaskName());
        assertEquals(3, entity.getPriority());
    }

}
