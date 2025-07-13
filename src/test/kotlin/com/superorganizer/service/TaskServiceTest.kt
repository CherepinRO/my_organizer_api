package com.superorganizer.service

import com.superorganizer.dto.TaskRequest
import com.superorganizer.dto.TaskResponse
import com.superorganizer.model.Priority
import com.superorganizer.model.Task
import com.superorganizer.model.TaskType
import com.superorganizer.repository.TaskRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestPropertySource(properties = ["spring.datasource.url=jdbc:h2:mem:testdb"])
class TaskServiceTest {
    
    private val taskRepository = mock<TaskRepository>()
    private val taskService = TaskService(taskRepository)
    
    private val sampleTask = Task(
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
    fun `createTask should save and return task response`() {
        // Given
        val taskToSave = Task(
            date = sampleTaskRequest.date,
            taskName = sampleTaskRequest.taskName,
            comment = sampleTaskRequest.comment,
            deadline = sampleTaskRequest.deadline,
            priority = sampleTaskRequest.priority,
            taskType = sampleTaskRequest.taskType
        )
        
        whenever(taskRepository.save(any<Task>())).thenReturn(sampleTask)
        
        // When
        val result = taskService.createTask(sampleTaskRequest)
        
        // Then
        assertNotNull(result)
        assertEquals(sampleTask.id, result.id)
        assertEquals(sampleTask.taskName, result.taskName)
        assertEquals(sampleTask.priority, result.priority)
        assertEquals(sampleTask.taskType, result.taskType)
        
        verify(taskRepository).save(any<Task>())
    }
    
    @Test
    fun `getAllTasks should return all tasks`() {
        // Given
        val tasks = listOf(sampleTask, sampleTask.copy(id = 2L, taskName = "Task 2"))
        whenever(taskRepository.findAll()).thenReturn(tasks)
        
        // When
        val result = taskService.getAllTasks()
        
        // Then
        assertEquals(2, result.size)
        assertEquals(sampleTask.taskName, result[0].taskName)
        assertEquals("Task 2", result[1].taskName)
        
        verify(taskRepository).findAll()
    }
    
    @Test
    fun `getTaskById should return task when found`() {
        // Given
        whenever(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask))
        
        // When
        val result = taskService.getTaskById(1L)
        
        // Then
        assertNotNull(result)
        assertEquals(sampleTask.id, result.id)
        assertEquals(sampleTask.taskName, result.taskName)
        
        verify(taskRepository).findById(1L)
    }
    
    @Test
    fun `getTaskById should throw exception when not found`() {
        // Given
        whenever(taskRepository.findById(999L)).thenReturn(Optional.empty())
        
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            taskService.getTaskById(999L)
        }
        
        assertEquals("Task not found with id: 999", exception.message)
        verify(taskRepository).findById(999L)
    }
    
    @Test
    fun `updateTask should update and return task`() {
        // Given
        val updatedTaskRequest = sampleTaskRequest.copy(
            taskName = "Updated Task",
            priority = Priority.LOW
        )
        val updatedTask = sampleTask.copy(
            taskName = "Updated Task",
            priority = Priority.LOW,
            updatedAt = LocalDateTime.now()
        )
        
        whenever(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask))
        whenever(taskRepository.save(any<Task>())).thenReturn(updatedTask)
        
        // When
        val result = taskService.updateTask(1L, updatedTaskRequest)
        
        // Then
        assertEquals(updatedTask.taskName, result.taskName)
        assertEquals(updatedTask.priority, result.priority)
        
        verify(taskRepository).findById(1L)
        verify(taskRepository).save(any<Task>())
    }
    
    @Test
    fun `updateTask should throw exception when task not found`() {
        // Given
        whenever(taskRepository.findById(999L)).thenReturn(Optional.empty())
        
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            taskService.updateTask(999L, sampleTaskRequest)
        }
        
        assertEquals("Task not found with id: 999", exception.message)
        verify(taskRepository).findById(999L)
        verify(taskRepository, never()).save(any<Task>())
    }
    
    @Test
    fun `deleteTask should delete when task exists`() {
        // Given
        whenever(taskRepository.existsById(1L)).thenReturn(true)
        
        // When
        taskService.deleteTask(1L)
        
        // Then
        verify(taskRepository).existsById(1L)
        verify(taskRepository).deleteById(1L)
    }
    
    @Test
    fun `deleteTask should throw exception when task not found`() {
        // Given
        whenever(taskRepository.existsById(999L)).thenReturn(false)
        
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            taskService.deleteTask(999L)
        }
        
        assertEquals("Task not found with id: 999", exception.message)
        verify(taskRepository).existsById(999L)
        verify(taskRepository, never()).deleteById(999L)
    }
    
    @Test
    fun `searchTasks should call repository with correct parameters`() {
        // Given
        val tasks = listOf(sampleTask)
        whenever(taskRepository.findTasksByFilters(
            taskName = "Test",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 31)
        )).thenReturn(tasks)
        
        // When
        val result = taskService.searchTasks(
            taskName = "Test",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 31)
        )
        
        // Then
        assertEquals(1, result.size)
        assertEquals(sampleTask.taskName, result[0].taskName)
        
        verify(taskRepository).findTasksByFilters(
            taskName = "Test",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 31)
        )
    }
    
    @Test
    fun `getTasksByPriority should return filtered tasks`() {
        // Given
        val highPriorityTasks = listOf(sampleTask)
        whenever(taskRepository.findByPriority(Priority.HIGH)).thenReturn(highPriorityTasks)
        
        // When
        val result = taskService.getTasksByPriority(Priority.HIGH)
        
        // Then
        assertEquals(1, result.size)
        assertEquals(Priority.HIGH, result[0].priority)
        
        verify(taskRepository).findByPriority(Priority.HIGH)
    }
    
    @Test
    fun `getTasksByType should return filtered tasks`() {
        // Given
        val workTasks = listOf(sampleTask)
        whenever(taskRepository.findByTaskType(TaskType.WORK)).thenReturn(workTasks)
        
        // When
        val result = taskService.getTasksByType(TaskType.WORK)
        
        // Then
        assertEquals(1, result.size)
        assertEquals(TaskType.WORK, result[0].taskType)
        
        verify(taskRepository).findByTaskType(TaskType.WORK)
    }
    
    @Test
    fun `getTasksByDeadline should return tasks with deadline when hasDeadline is true`() {
        // Given
        val tasksWithDeadline = listOf(sampleTask)
        whenever(taskRepository.findByDeadlineIsNotNull()).thenReturn(tasksWithDeadline)
        
        // When
        val result = taskService.getTasksByDeadline(true)
        
        // Then
        assertEquals(1, result.size)
        assertNotNull(result[0].deadline)
        
        verify(taskRepository).findByDeadlineIsNotNull()
    }
    
    @Test
    fun `getTasksByDeadline should return tasks without deadline when hasDeadline is false`() {
        // Given
        val tasksWithoutDeadline = listOf(sampleTask.copy(deadline = null))
        whenever(taskRepository.findByDeadlineIsNull()).thenReturn(tasksWithoutDeadline)
        
        // When
        val result = taskService.getTasksByDeadline(false)
        
        // Then
        assertEquals(1, result.size)
        assertEquals(null, result[0].deadline)
        
        verify(taskRepository).findByDeadlineIsNull()
    }
    
    @Test
    fun `getTasksOrderedByDeadline should return ordered tasks`() {
        // Given
        val orderedTasks = listOf(sampleTask)
        whenever(taskRepository.findByOrderByDeadlineAsc()).thenReturn(orderedTasks)
        
        // When
        val result = taskService.getTasksOrderedByDeadline()
        
        // Then
        assertEquals(1, result.size)
        
        verify(taskRepository).findByOrderByDeadlineAsc()
    }
    
    @Test
    fun `getTasksOrderedByPriority should return ordered tasks`() {
        // Given
        val orderedTasks = listOf(sampleTask)
        whenever(taskRepository.findByOrderByPriorityAsc()).thenReturn(orderedTasks)
        
        // When
        val result = taskService.getTasksOrderedByPriority()
        
        // Then
        assertEquals(1, result.size)
        
        verify(taskRepository).findByOrderByPriorityAsc()
    }
    
    @Test
    fun `getTasksOrderedByDate should return ordered tasks`() {
        // Given
        val orderedTasks = listOf(sampleTask)
        whenever(taskRepository.findByOrderByDateDesc()).thenReturn(orderedTasks)
        
        // When
        val result = taskService.getTasksOrderedByDate()
        
        // Then
        assertEquals(1, result.size)
        
        verify(taskRepository).findByOrderByDateDesc()
    }
}