package com.superorganizer.dto

import com.superorganizer.model.Priority
import com.superorganizer.model.Task
import com.superorganizer.model.TaskType
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TaskResponseTest {
    
    private val sampleTask = Task(
        id = 1L,
        date = LocalDate.of(2024, 1, 15),
        taskName = "Test Task",
        comment = "Test comment",
        deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
        priority = Priority.HIGH,
        taskType = TaskType.WORK,
        createdAt = LocalDateTime.of(2024, 1, 10, 10, 0),
        updatedAt = LocalDateTime.of(2024, 1, 12, 14, 30)
    )
    
    @Test
    fun `TaskResponse from should create response from task entity`() {
        // When
        val response = TaskResponse.from(sampleTask)
        
        // Then
        assertNotNull(response)
        assertEquals(1L, response.id)
        assertEquals(LocalDate.of(2024, 1, 15), response.date)
        assertEquals("Test Task", response.taskName)
        assertEquals("Test comment", response.comment)
        assertEquals(LocalDateTime.of(2024, 1, 20, 17, 0), response.deadline)
        assertEquals(Priority.HIGH, response.priority)
        assertEquals(TaskType.WORK, response.taskType)
        assertEquals(LocalDateTime.of(2024, 1, 10, 10, 0), response.createdAt)
        assertEquals(LocalDateTime.of(2024, 1, 12, 14, 30), response.updatedAt)
    }
    
    @Test
    fun `TaskResponse from should handle null comment`() {
        // Given
        val taskWithNullComment = sampleTask.copy(comment = null)
        
        // When
        val response = TaskResponse.from(taskWithNullComment)
        
        // Then
        assertEquals(null, response.comment)
    }
    
    @Test
    fun `TaskResponse from should handle null deadline`() {
        // Given
        val taskWithNullDeadline = sampleTask.copy(deadline = null)
        
        // When
        val response = TaskResponse.from(taskWithNullDeadline)
        
        // Then
        assertEquals(null, response.deadline)
    }
    
    @Test
    fun `TaskResponse from should handle all priority types`() {
        // Test HIGH priority
        val highPriorityResponse = TaskResponse.from(sampleTask.copy(priority = Priority.HIGH))
        assertEquals(Priority.HIGH, highPriorityResponse.priority)
        
        // Test MEDIUM priority
        val mediumPriorityResponse = TaskResponse.from(sampleTask.copy(priority = Priority.MEDIUM))
        assertEquals(Priority.MEDIUM, mediumPriorityResponse.priority)
        
        // Test LOW priority
        val lowPriorityResponse = TaskResponse.from(sampleTask.copy(priority = Priority.LOW))
        assertEquals(Priority.LOW, lowPriorityResponse.priority)
    }
    
    @Test
    fun `TaskResponse from should handle all task types`() {
        // Test WORK type
        val workTypeResponse = TaskResponse.from(sampleTask.copy(taskType = TaskType.WORK))
        assertEquals(TaskType.WORK, workTypeResponse.taskType)
        
        // Test HOME type
        val homeTypeResponse = TaskResponse.from(sampleTask.copy(taskType = TaskType.HOME))
        assertEquals(TaskType.HOME, homeTypeResponse.taskType)
    }
    
    @Test
    fun `TaskResponse should have correct data class properties`() {
        // Given
        val response = TaskResponse.from(sampleTask)
        
        // When
        val copy = response.copy(taskName = "Updated Task Name")
        
        // Then
        assertEquals("Updated Task Name", copy.taskName)
        assertEquals(sampleTask.taskName, response.taskName) // Original should remain unchanged
    }
    
    @Test
    fun `TaskResponse should handle minimum required fields`() {
        // Given
        val minimalTask = Task(
            id = 2L,
            date = LocalDate.of(2024, 2, 1),
            taskName = "Minimal Task",
            comment = null,
            deadline = null,
            priority = Priority.MEDIUM,
            taskType = TaskType.HOME,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // When
        val response = TaskResponse.from(minimalTask)
        
        // Then
        assertEquals(2L, response.id)
        assertEquals("Minimal Task", response.taskName)
        assertEquals(null, response.comment)
        assertEquals(null, response.deadline)
        assertEquals(Priority.MEDIUM, response.priority)
        assertEquals(TaskType.HOME, response.taskType)
    }
}