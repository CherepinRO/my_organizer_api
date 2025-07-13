package com.superorganizer.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.superorganizer.dto.TaskRequest
import com.superorganizer.dto.TaskResponse
import com.superorganizer.model.Priority
import com.superorganizer.model.TaskType
import com.superorganizer.service.TaskService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(TaskController::class)
class TaskControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var taskService: TaskService
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    private val sampleTaskResponse = TaskResponse(
        id = 1L,
        date = LocalDate.of(2024, 1, 15),
        taskName = "Test Task",
        comment = "Test comment",
        deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
        priority = Priority.HIGH,
        taskType = TaskType.WORK,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    private val sampleTaskRequest = TaskRequest(
        date = LocalDate.of(2024, 1, 15),
        taskName = "Test Task",
        comment = "Test comment",
        deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
        priority = Priority.HIGH,
        taskType = TaskType.WORK
    )
    
    @Test
    fun `POST api_tasks should create task and return 201`() {
        // Given
        whenever(taskService.createTask(any())).thenReturn(sampleTaskResponse)
        
        // When & Then
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleTaskRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.taskName").value("Test Task"))
            .andExpect(jsonPath("$.priority").value("HIGH"))
            .andExpect(jsonPath("$.taskType").value("WORK"))
        
        verify(taskService).createTask(any())
    }
    
    @Test
    fun `POST api_tasks should return 400 for invalid request`() {
        // Given
        val invalidRequest = TaskRequest(
            date = LocalDate.of(2024, 1, 15),
            taskName = "", // Empty task name should fail validation
            comment = "Test comment",
            deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
            priority = Priority.HIGH,
            taskType = TaskType.WORK
        )
        
        // When & Then
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest)
        
        verify(taskService, never()).createTask(any())
    }
    
    @Test
    fun `GET api_tasks should return all tasks`() {
        // Given
        val tasks = listOf(sampleTaskResponse, sampleTaskResponse.copy(id = 2L, taskName = "Task 2"))
        whenever(taskService.getAllTasks()).thenReturn(tasks)
        
        // When & Then
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].taskName").value("Test Task"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].taskName").value("Task 2"))
        
        verify(taskService).getAllTasks()
    }
    
    @Test
    fun `GET api_tasks_id should return task by id`() {
        // Given
        whenever(taskService.getTaskById(1L)).thenReturn(sampleTaskResponse)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.taskName").value("Test Task"))
        
        verify(taskService).getTaskById(1L)
    }
    
    @Test
    fun `GET api_tasks_id should return 404 for non-existent task`() {
        // Given
        whenever(taskService.getTaskById(999L)).thenThrow(IllegalArgumentException("Task not found with id: 999"))
        
        // When & Then
        mockMvc.perform(get("/api/tasks/999"))
            .andExpect(status().isNotFound)
        
        verify(taskService).getTaskById(999L)
    }
    
    @Test
    fun `PUT api_tasks_id should update task and return 200`() {
        // Given
        val updatedResponse = sampleTaskResponse.copy(taskName = "Updated Task")
        whenever(taskService.updateTask(eq(1L), any())).thenReturn(updatedResponse)
        
        // When & Then
        mockMvc.perform(put("/api/tasks/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleTaskRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.taskName").value("Updated Task"))
        
        verify(taskService).updateTask(eq(1L), any())
    }
    
    @Test
    fun `PUT api_tasks_id should return 404 for non-existent task`() {
        // Given
        whenever(taskService.updateTask(eq(999L), any())).thenThrow(IllegalArgumentException("Task not found with id: 999"))
        
        // When & Then
        mockMvc.perform(put("/api/tasks/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleTaskRequest)))
            .andExpect(status().isNotFound)
        
        verify(taskService).updateTask(eq(999L), any())
    }
    
    @Test
    fun `DELETE api_tasks_id should delete task and return 204`() {
        // Given
        doNothing().whenever(taskService).deleteTask(1L)
        
        // When & Then
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent)
        
        verify(taskService).deleteTask(1L)
    }
    
    @Test
    fun `DELETE api_tasks_id should return 404 for non-existent task`() {
        // Given
        whenever(taskService.deleteTask(999L)).thenThrow(IllegalArgumentException("Task not found with id: 999"))
        
        // When & Then
        mockMvc.perform(delete("/api/tasks/999"))
            .andExpect(status().isNotFound)
        
        verify(taskService).deleteTask(999L)
    }
    
    @Test
    fun `GET api_tasks_search should search tasks with filters`() {
        // Given
        val searchResults = listOf(sampleTaskResponse)
        whenever(taskService.searchTasks(
            taskName = "Test",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 31)
        )).thenReturn(searchResults)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/search")
            .param("taskName", "Test")
            .param("priority", "HIGH")
            .param("taskType", "WORK")
            .param("startDate", "2024-01-01")
            .param("endDate", "2024-01-31"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].taskName").value("Test Task"))
        
        verify(taskService).searchTasks(
            taskName = "Test",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 31)
        )
    }
    
    @Test
    fun `GET api_tasks_search should work with no filters`() {
        // Given
        val searchResults = listOf(sampleTaskResponse)
        whenever(taskService.searchTasks(
            taskName = null,
            priority = null,
            taskType = null,
            startDate = null,
            endDate = null
        )).thenReturn(searchResults)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/search"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
        
        verify(taskService).searchTasks(
            taskName = null,
            priority = null,
            taskType = null,
            startDate = null,
            endDate = null
        )
    }
    
    @Test
    fun `GET api_tasks_priority_priority should return tasks by priority`() {
        // Given
        val highPriorityTasks = listOf(sampleTaskResponse)
        whenever(taskService.getTasksByPriority(Priority.HIGH)).thenReturn(highPriorityTasks)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/priority/HIGH"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].priority").value("HIGH"))
        
        verify(taskService).getTasksByPriority(Priority.HIGH)
    }
    
    @Test
    fun `GET api_tasks_type_taskType should return tasks by type`() {
        // Given
        val workTasks = listOf(sampleTaskResponse)
        whenever(taskService.getTasksByType(TaskType.WORK)).thenReturn(workTasks)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/type/WORK"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].taskType").value("WORK"))
        
        verify(taskService).getTasksByType(TaskType.WORK)
    }
    
    @Test
    fun `GET api_tasks_deadline should return tasks by deadline status`() {
        // Given
        val tasksWithDeadline = listOf(sampleTaskResponse)
        whenever(taskService.getTasksByDeadline(true)).thenReturn(tasksWithDeadline)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/deadline").param("hasDeadline", "true"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
        
        verify(taskService).getTasksByDeadline(true)
    }
    
    @Test
    fun `GET api_tasks_deadline should use default value for hasDeadline`() {
        // Given
        val tasksWithDeadline = listOf(sampleTaskResponse)
        whenever(taskService.getTasksByDeadline(true)).thenReturn(tasksWithDeadline)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/deadline"))
            .andExpect(status().isOk)
        
        verify(taskService).getTasksByDeadline(true) // Default value is true
    }
    
    @Test
    fun `GET api_tasks_ordered_deadline should return tasks ordered by deadline`() {
        // Given
        val orderedTasks = listOf(sampleTaskResponse)
        whenever(taskService.getTasksOrderedByDeadline()).thenReturn(orderedTasks)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/ordered/deadline"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
        
        verify(taskService).getTasksOrderedByDeadline()
    }
    
    @Test
    fun `GET api_tasks_ordered_priority should return tasks ordered by priority`() {
        // Given
        val orderedTasks = listOf(sampleTaskResponse)
        whenever(taskService.getTasksOrderedByPriority()).thenReturn(orderedTasks)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/ordered/priority"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
        
        verify(taskService).getTasksOrderedByPriority()
    }
    
    @Test
    fun `GET api_tasks_ordered_date should return tasks ordered by date`() {
        // Given
        val orderedTasks = listOf(sampleTaskResponse)
        whenever(taskService.getTasksOrderedByDate()).thenReturn(orderedTasks)
        
        // When & Then
        mockMvc.perform(get("/api/tasks/ordered/date"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
        
        verify(taskService).getTasksOrderedByDate()
    }
    
    @Test
    fun `POST api_tasks should handle validation errors correctly`() {
        // Given
        val invalidRequest = mapOf(
            "taskName" to "", // Empty task name
            "priority" to "INVALID", // Invalid priority
            "taskType" to "INVALID" // Invalid task type
        )
        
        // When & Then
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest)
        
        verify(taskService, never()).createTask(any())
    }
}