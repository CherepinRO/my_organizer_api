package com.superorganizer.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.superorganizer.dto.TaskRequest
import com.superorganizer.model.Priority
import com.superorganizer.model.TaskType
import com.superorganizer.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = ["spring.datasource.url=jdbc:h2:mem:integrationtestdb"])
@Transactional
class TaskControllerIntegrationTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var taskRepository: TaskRepository
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    private val sampleTaskRequest = TaskRequest(
        date = LocalDate.of(2024, 1, 15),
        taskName = "Integration Test Task",
        comment = "Integration test comment",
        deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
        priority = Priority.HIGH,
        taskType = TaskType.WORK
    )
    
    @BeforeEach
    fun setUp() {
        taskRepository.deleteAll()
    }
    
    @Test
    fun `complete task lifecycle - create, read, update, delete`() {
        // 1. Create task
        val createResponse = mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleTaskRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.taskName").value("Integration Test Task"))
            .andExpect(jsonPath("$.priority").value("HIGH"))
            .andExpect(jsonPath("$.taskType").value("WORK"))
            .andReturn()
        
        val createdTask = objectMapper.readTree(createResponse.response.contentAsString)
        val taskId = createdTask["id"].asLong()
        
        // 2. Read task by ID
        mockMvc.perform(get("/api/tasks/$taskId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(taskId))
            .andExpect(jsonPath("$.taskName").value("Integration Test Task"))
        
        // 3. Update task
        val updatedRequest = sampleTaskRequest.copy(
            taskName = "Updated Integration Test Task",
            priority = Priority.LOW
        )
        
        mockMvc.perform(put("/api/tasks/$taskId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.taskName").value("Updated Integration Test Task"))
            .andExpect(jsonPath("$.priority").value("LOW"))
        
        // 4. Verify update
        mockMvc.perform(get("/api/tasks/$taskId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.taskName").value("Updated Integration Test Task"))
            .andExpect(jsonPath("$.priority").value("LOW"))
        
        // 5. Delete task
        mockMvc.perform(delete("/api/tasks/$taskId"))
            .andExpect(status().isNoContent)
        
        // 6. Verify deletion
        mockMvc.perform(get("/api/tasks/$taskId"))
            .andExpect(status().isNotFound)
    }
    
    @Test
    fun `search functionality should work with multiple criteria`() {
        // Given - Create multiple tasks
        val task1 = sampleTaskRequest.copy(
            taskName = "Important Meeting",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            date = LocalDate.of(2024, 1, 15)
        )
        val task2 = sampleTaskRequest.copy(
            taskName = "Home Cleaning",
            priority = Priority.LOW,
            taskType = TaskType.HOME,
            date = LocalDate.of(2024, 1, 16)
        )
        val task3 = sampleTaskRequest.copy(
            taskName = "Important Planning",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            date = LocalDate.of(2024, 1, 17)
        )
        
        // Create tasks
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task1)))
            .andExpect(status().isCreated)
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task2)))
            .andExpect(status().isCreated)
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task3)))
            .andExpect(status().isCreated)
        
        // Test search by task name
        mockMvc.perform(get("/api/tasks/search")
            .param("taskName", "important"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
        
        // Test search by priority
        mockMvc.perform(get("/api/tasks/search")
            .param("priority", "HIGH"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
        
        // Test search by task type
        mockMvc.perform(get("/api/tasks/search")
            .param("taskType", "WORK"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
        
        // Test combined search
        mockMvc.perform(get("/api/tasks/search")
            .param("taskName", "important")
            .param("priority", "HIGH")
            .param("taskType", "WORK"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
        
        // Test date range search
        mockMvc.perform(get("/api/tasks/search")
            .param("startDate", "2024-01-14")
            .param("endDate", "2024-01-16"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }
    
    @Test
    fun `filtering endpoints should work correctly`() {
        // Given - Create tasks with different priorities and types
        val highPriorityTask = sampleTaskRequest.copy(
            taskName = "High Priority Task",
            priority = Priority.HIGH,
            taskType = TaskType.WORK
        )
        val lowPriorityTask = sampleTaskRequest.copy(
            taskName = "Low Priority Task",
            priority = Priority.LOW,
            taskType = TaskType.HOME
        )
        
        // Create tasks
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(highPriorityTask)))
            .andExpect(status().isCreated)
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(lowPriorityTask)))
            .andExpect(status().isCreated)
        
        // Test filter by priority
        mockMvc.perform(get("/api/tasks/priority/HIGH"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].priority").value("HIGH"))
        
        // Test filter by task type
        mockMvc.perform(get("/api/tasks/type/WORK"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].taskType").value("WORK"))
        
        // Test filter by deadline status
        mockMvc.perform(get("/api/tasks/deadline")
            .param("hasDeadline", "true"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
        
        // Test filter for tasks without deadline
        val taskWithoutDeadline = sampleTaskRequest.copy(
            taskName = "Task Without Deadline",
            deadline = null
        )
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(taskWithoutDeadline)))
            .andExpect(status().isCreated)
        
        mockMvc.perform(get("/api/tasks/deadline")
            .param("hasDeadline", "false"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].taskName").value("Task Without Deadline"))
    }
    
    @Test
    fun `ordering endpoints should work correctly`() {
        // Given - Create tasks with different dates, priorities, and deadlines
        val task1 = sampleTaskRequest.copy(
            taskName = "Task 1",
            date = LocalDate.of(2024, 1, 10),
            priority = Priority.HIGH,
            deadline = LocalDateTime.of(2024, 1, 25, 10, 0)
        )
        val task2 = sampleTaskRequest.copy(
            taskName = "Task 2",
            date = LocalDate.of(2024, 1, 20),
            priority = Priority.LOW,
            deadline = LocalDateTime.of(2024, 1, 15, 15, 0)
        )
        val task3 = sampleTaskRequest.copy(
            taskName = "Task 3",
            date = LocalDate.of(2024, 1, 15),
            priority = Priority.MEDIUM,
            deadline = LocalDateTime.of(2024, 1, 30, 12, 0)
        )
        
        // Create tasks
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task1)))
            .andExpect(status().isCreated)
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task2)))
            .andExpect(status().isCreated)
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task3)))
            .andExpect(status().isCreated)
        
        // Test order by deadline (ascending)
        mockMvc.perform(get("/api/tasks/ordered/deadline"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].taskName").value("Task 2")) // Earliest deadline
            .andExpect(jsonPath("$[1].taskName").value("Task 1"))
            .andExpect(jsonPath("$[2].taskName").value("Task 3")) // Latest deadline
        
        // Test order by priority (ascending - HIGH, LOW, MEDIUM)
        mockMvc.perform(get("/api/tasks/ordered/priority"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].priority").value("HIGH"))
            .andExpect(jsonPath("$[1].priority").value("LOW"))
            .andExpect(jsonPath("$[2].priority").value("MEDIUM"))
        
        // Test order by date (descending)
        mockMvc.perform(get("/api/tasks/ordered/date"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].taskName").value("Task 2")) // Latest date
            .andExpect(jsonPath("$[1].taskName").value("Task 3"))
            .andExpect(jsonPath("$[2].taskName").value("Task 1")) // Earliest date
    }
    
    @Test
    fun `error handling should work correctly`() {
        // Test 404 for non-existent task
        mockMvc.perform(get("/api/tasks/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Not Found"))
        
        // Test 400 for invalid request
        val invalidRequest = mapOf(
            "taskName" to "", // Empty task name
            "priority" to "INVALID_PRIORITY"
        )
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest)
        
        // Test 404 for update non-existent task
        mockMvc.perform(put("/api/tasks/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleTaskRequest)))
            .andExpect(status().isNotFound)
        
        // Test 404 for delete non-existent task
        mockMvc.perform(delete("/api/tasks/999"))
            .andExpect(status().isNotFound)
    }
    
    @Test
    fun `validation should work correctly`() {
        // Test missing required fields
        val invalidRequest = TaskRequest(
            date = LocalDate.of(2024, 1, 15),
            taskName = "", // Empty task name should fail
            comment = "Test comment",
            deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
            priority = Priority.HIGH,
            taskType = TaskType.WORK
        )
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Validation Failed"))
        
        // Test valid request with minimal fields
        val minimalRequest = TaskRequest(
            date = LocalDate.of(2024, 1, 15),
            taskName = "Valid Task",
            comment = null,
            deadline = null,
            priority = Priority.MEDIUM,
            taskType = TaskType.HOME
        )
        
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(minimalRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.taskName").value("Valid Task"))
            .andExpect(jsonPath("$.comment").isEmpty)
            .andExpect(jsonPath("$.deadline").isEmpty)
    }
}