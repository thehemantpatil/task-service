package com.bond.taskservice.api.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void health_shouldBeUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void post_shouldCreateTasks_andReturn201_andRequestIdHeader() throws Exception {
        String payload = """
                [
                  {"taskName":"Write tests","priority":4},
                  {"taskName":"Add logging","priority":2}
                ]
                """;

        mockMvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                // Day 4: request id header exists even if client doesn't send it
                .andExpect(header().exists("X-Request-Id"))
                // Response should be array with 2 items
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].taskName").value("Write tests"))
                .andExpect(jsonPath("$[0].priority").value(3))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
    }

    @Test
    void post_shouldReturn400_onValidationError_withStandardErrorShape() throws Exception {
        // taskName blank + priority out of range
        String payload = """
                [
                  {"taskName":"", "priority":10}
                ]
                """;

        mockMvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/v1/tasks"))
                .andExpect(jsonPath("$.details", not(empty())))
                .andExpect(jsonPath("$.details[*].field", hasItems("taskName", "priority")));
    }

    @Test
    void get_shouldReturn404_whenTaskNotFound() throws Exception {
        mockMvc.perform(get("/v1/tasks/999999"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/v1/tasks/999999"))
                .andExpect(jsonPath("$.message", containsString("Task not found")));
    }

    @Test
    void delete_shouldReturn204_whenDeleted() throws Exception {
        // create 1 task first
        String payload = """
                [
                  {"taskName":"Temp Task","priority":1}
                ]
                """;

        String response = mockMvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract id without adding extra libs: simple parse
        // Response looks like: [{"id":1001,...}]
        long id = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(delete("/v1/tasks/" + id))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("X-Request-Id"));

        // now ensure it's 404
        mockMvc.perform(get("/v1/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void requestId_shouldEchoClientProvidedRequestId() throws Exception {
        mockMvc.perform(get("/v1/tasks")
                        .header("X-Request-Id", "hemant-req-123"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", "hemant-req-123"));
    }

}
