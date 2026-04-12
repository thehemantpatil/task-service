package com.bond.taskservice.api.exception;


public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task not found for id=" + id);
    }
}

