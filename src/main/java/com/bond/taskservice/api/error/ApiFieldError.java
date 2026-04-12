package com.bond.taskservice.api.error;


public class ApiFieldError {

    private final String field;
    private final String issue;

    public ApiFieldError(String field, String issue) {
        this.field = field;
        this.issue = issue;
    }

    public String getField() {
        return field;
    }

    public String getIssue() {
        return issue;
    }
}

