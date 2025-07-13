package com.superorganizer.repository

import com.superorganizer.model.Priority
import com.superorganizer.model.Task
import com.superorganizer.model.TaskType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
@TestPropertySource(properties = ["spring.datasource.url=jdbc:h2:mem:testdb"])
class TaskRepositoryTest {
    
    @Autowired
    private lateinit var taskRepository: TaskRepository
    
    private fun createSampleTask(
        taskName: String = "Test Task",
        priority: Priority = Priority.MEDIUM,
        taskType: TaskType = TaskType.WORK,
        date: LocalDate = LocalDate.of(2024, 1, 15),
        deadline: LocalDateTime? = null
    ): Task {
        return Task(
            date = date,
            taskName = taskName,
            comment = "Test comment",
            deadline = deadline,
            priority = priority,
            taskType = taskType
        )
    }
    
    @Test
    fun `findByTaskNameContainingIgnoreCase should return matching tasks`() {
        // Given
        val task1 = createSampleTask(taskName = "Important Meeting")
        val task2 = createSampleTask(taskName = "Code Review")
        val task3 = createSampleTask(taskName = "important planning")
        
        taskRepository.saveAll(listOf(task1, task2, task3))
        
        // When
        val result = taskRepository.findByTaskNameContainingIgnoreCase("important")
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.taskName == "Important Meeting" })
        assertTrue(result.any { it.taskName == "important planning" })
    }
    
    @Test
    fun `findByPriority should return tasks with matching priority`() {
        // Given
        val highTask = createSampleTask(priority = Priority.HIGH)
        val mediumTask = createSampleTask(priority = Priority.MEDIUM)
        val lowTask = createSampleTask(priority = Priority.LOW)
        
        taskRepository.saveAll(listOf(highTask, mediumTask, lowTask))
        
        // When
        val highPriorityTasks = taskRepository.findByPriority(Priority.HIGH)
        val mediumPriorityTasks = taskRepository.findByPriority(Priority.MEDIUM)
        
        // Then
        assertEquals(1, highPriorityTasks.size)
        assertEquals(Priority.HIGH, highPriorityTasks[0].priority)
        
        assertEquals(1, mediumPriorityTasks.size)
        assertEquals(Priority.MEDIUM, mediumPriorityTasks[0].priority)
    }
    
    @Test
    fun `findByTaskType should return tasks with matching type`() {
        // Given
        val workTask = createSampleTask(taskType = TaskType.WORK)
        val homeTask = createSampleTask(taskType = TaskType.HOME)
        
        taskRepository.saveAll(listOf(workTask, homeTask))
        
        // When
        val workTasks = taskRepository.findByTaskType(TaskType.WORK)
        val homeTasks = taskRepository.findByTaskType(TaskType.HOME)
        
        // Then
        assertEquals(1, workTasks.size)
        assertEquals(TaskType.WORK, workTasks[0].taskType)
        
        assertEquals(1, homeTasks.size)
        assertEquals(TaskType.HOME, homeTasks[0].taskType)
    }
    
    @Test
    fun `findByDateBetween should return tasks within date range`() {
        // Given
        val task1 = createSampleTask(date = LocalDate.of(2024, 1, 10))
        val task2 = createSampleTask(date = LocalDate.of(2024, 1, 15))
        val task3 = createSampleTask(date = LocalDate.of(2024, 1, 20))
        val task4 = createSampleTask(date = LocalDate.of(2024, 1, 25))
        
        taskRepository.saveAll(listOf(task1, task2, task3, task4))
        
        // When
        val result = taskRepository.findByDateBetween(
            LocalDate.of(2024, 1, 12),
            LocalDate.of(2024, 1, 22)
        )
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.date == LocalDate.of(2024, 1, 15) })
        assertTrue(result.any { it.date == LocalDate.of(2024, 1, 20) })
    }
    
    @Test
    fun `findByDeadlineIsNull should return tasks without deadline`() {
        // Given
        val taskWithDeadline = createSampleTask(deadline = LocalDateTime.of(2024, 1, 20, 17, 0))
        val taskWithoutDeadline = createSampleTask(deadline = null)
        
        taskRepository.saveAll(listOf(taskWithDeadline, taskWithoutDeadline))
        
        // When
        val result = taskRepository.findByDeadlineIsNull()
        
        // Then
        assertEquals(1, result.size)
        assertEquals(null, result[0].deadline)
    }
    
    @Test
    fun `findByDeadlineIsNotNull should return tasks with deadline`() {
        // Given
        val taskWithDeadline = createSampleTask(deadline = LocalDateTime.of(2024, 1, 20, 17, 0))
        val taskWithoutDeadline = createSampleTask(deadline = null)
        
        taskRepository.saveAll(listOf(taskWithDeadline, taskWithoutDeadline))
        
        // When
        val result = taskRepository.findByDeadlineIsNotNull()
        
        // Then
        assertEquals(1, result.size)
        assertEquals(LocalDateTime.of(2024, 1, 20, 17, 0), result[0].deadline)
    }
    
    @Test
    fun `findTasksByFilters should return tasks matching all filters`() {
        // Given
        val task1 = createSampleTask(
            taskName = "Important Meeting",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            date = LocalDate.of(2024, 1, 15)
        )
        val task2 = createSampleTask(
            taskName = "Home Cleaning",
            priority = Priority.LOW,
            taskType = TaskType.HOME,
            date = LocalDate.of(2024, 1, 16)
        )
        val task3 = createSampleTask(
            taskName = "Important Planning",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            date = LocalDate.of(2024, 1, 17)
        )
        
        taskRepository.saveAll(listOf(task1, task2, task3))
        
        // When
        val result = taskRepository.findTasksByFilters(
            taskName = "important",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            startDate = LocalDate.of(2024, 1, 10),
            endDate = LocalDate.of(2024, 1, 20)
        )
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.priority == Priority.HIGH })
        assertTrue(result.all { it.taskType == TaskType.WORK })
        assertTrue(result.all { it.taskName.contains("Important", ignoreCase = true) })
    }
    
    @Test
    fun `findTasksByFilters should return all tasks when no filters provided`() {
        // Given
        val task1 = createSampleTask(taskName = "Task 1")
        val task2 = createSampleTask(taskName = "Task 2")
        val task3 = createSampleTask(taskName = "Task 3")
        
        taskRepository.saveAll(listOf(task1, task2, task3))
        
        // When
        val result = taskRepository.findTasksByFilters(
            taskName = null,
            priority = null,
            taskType = null,
            startDate = null,
            endDate = null
        )
        
        // Then
        assertEquals(3, result.size)
    }
    
    @Test
    fun `findByOrderByDeadlineAsc should return tasks ordered by deadline`() {
        // Given
        val task1 = createSampleTask(
            taskName = "Task 1",
            deadline = LocalDateTime.of(2024, 1, 25, 10, 0)
        )
        val task2 = createSampleTask(
            taskName = "Task 2",
            deadline = LocalDateTime.of(2024, 1, 20, 15, 0)
        )
        val task3 = createSampleTask(
            taskName = "Task 3",
            deadline = null
        )
        
        taskRepository.saveAll(listOf(task1, task2, task3))
        
        // When
        val result = taskRepository.findByOrderByDeadlineAsc()
        
        // Then
        assertEquals(3, result.size)
        // Tasks with null deadline should come first, then ordered by deadline
        assertTrue(result[0].deadline == null)
        assertEquals(LocalDateTime.of(2024, 1, 20, 15, 0), result[1].deadline)
        assertEquals(LocalDateTime.of(2024, 1, 25, 10, 0), result[2].deadline)
    }
    
    @Test
    fun `findByOrderByPriorityAsc should return tasks ordered by priority`() {
        // Given
        val highTask = createSampleTask(taskName = "High Task", priority = Priority.HIGH)
        val mediumTask = createSampleTask(taskName = "Medium Task", priority = Priority.MEDIUM)
        val lowTask = createSampleTask(taskName = "Low Task", priority = Priority.LOW)
        
        taskRepository.saveAll(listOf(highTask, mediumTask, lowTask))
        
        // When
        val result = taskRepository.findByOrderByPriorityAsc()
        
        // Then
        assertEquals(3, result.size)
        assertEquals(Priority.HIGH, result[0].priority)
        assertEquals(Priority.LOW, result[1].priority)
        assertEquals(Priority.MEDIUM, result[2].priority)
    }
    
    @Test
    fun `findByOrderByDateDesc should return tasks ordered by date descending`() {
        // Given
        val task1 = createSampleTask(taskName = "Task 1", date = LocalDate.of(2024, 1, 10))
        val task2 = createSampleTask(taskName = "Task 2", date = LocalDate.of(2024, 1, 20))
        val task3 = createSampleTask(taskName = "Task 3", date = LocalDate.of(2024, 1, 15))
        
        taskRepository.saveAll(listOf(task1, task2, task3))
        
        // When
        val result = taskRepository.findByOrderByDateDesc()
        
        // Then
        assertEquals(3, result.size)
        assertEquals(LocalDate.of(2024, 1, 20), result[0].date)
        assertEquals(LocalDate.of(2024, 1, 15), result[1].date)
        assertEquals(LocalDate.of(2024, 1, 10), result[2].date)
    }
    
    @Test
    fun `findByDeadlineBetween should return tasks within deadline range`() {
        // Given
        val task1 = createSampleTask(
            taskName = "Task 1",
            deadline = LocalDateTime.of(2024, 1, 15, 10, 0)
        )
        val task2 = createSampleTask(
            taskName = "Task 2",
            deadline = LocalDateTime.of(2024, 1, 20, 15, 0)
        )
        val task3 = createSampleTask(
            taskName = "Task 3",
            deadline = LocalDateTime.of(2024, 1, 25, 12, 0)
        )
        
        taskRepository.saveAll(listOf(task1, task2, task3))
        
        // When
        val result = taskRepository.findByDeadlineBetween(
            LocalDateTime.of(2024, 1, 18, 0, 0),
            LocalDateTime.of(2024, 1, 22, 23, 59)
        )
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Task 2", result[0].taskName)
    }
}