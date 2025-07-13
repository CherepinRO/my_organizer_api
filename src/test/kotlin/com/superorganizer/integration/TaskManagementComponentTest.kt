package com.superorganizer.integration

import com.superorganizer.dto.TaskRequest
import com.superorganizer.dto.TaskResponse
import com.superorganizer.model.Priority
import com.superorganizer.model.Task
import com.superorganizer.model.TaskType
import com.superorganizer.repository.TaskRepository
import com.superorganizer.service.TaskService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@TestPropertySource(properties = ["spring.datasource.url=jdbc:h2:mem:componenttestdb"])
@Transactional
class TaskManagementComponentTest {
    
    @Autowired
    private lateinit var taskService: TaskService
    
    @Autowired
    private lateinit var taskRepository: TaskRepository
    
    @BeforeEach
    fun setUp() {
        taskRepository.deleteAll()
    }
    
    @Test
    fun `complete task management workflow should work end-to-end`() {
        // 1. Create multiple tasks
        val task1 = TaskRequest(
            date = LocalDate.of(2024, 1, 15),
            taskName = "Important Meeting",
            comment = "Quarterly planning meeting",
            deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
            priority = Priority.HIGH,
            taskType = TaskType.WORK
        )
        
        val task2 = TaskRequest(
            date = LocalDate.of(2024, 1, 16),
            taskName = "Home Cleaning",
            comment = "Weekly house cleaning",
            deadline = null,
            priority = Priority.LOW,
            taskType = TaskType.HOME
        )
        
        val task3 = TaskRequest(
            date = LocalDate.of(2024, 1, 17),
            taskName = "Code Review",
            comment = "Review PR #123",
            deadline = LocalDateTime.of(2024, 1, 18, 10, 0),
            priority = Priority.MEDIUM,
            taskType = TaskType.WORK
        )
        
        // Create tasks
        val createdTask1 = taskService.createTask(task1)
        val createdTask2 = taskService.createTask(task2)
        val createdTask3 = taskService.createTask(task3)
        
        // Verify creation
        assertNotNull(createdTask1.id)
        assertNotNull(createdTask2.id)
        assertNotNull(createdTask3.id)
        
        // 2. Verify all tasks are retrievable
        val allTasks = taskService.getAllTasks()
        assertEquals(3, allTasks.size)
        
        // 3. Test individual task retrieval
        val retrievedTask1 = taskService.getTaskById(createdTask1.id)
        assertEquals("Important Meeting", retrievedTask1.taskName)
        assertEquals(Priority.HIGH, retrievedTask1.priority)
        
        // 4. Test task update
        val updatedTaskRequest = task1.copy(
            taskName = "Updated Meeting",
            priority = Priority.MEDIUM
        )
        val updatedTask = taskService.updateTask(createdTask1.id, updatedTaskRequest)
        assertEquals("Updated Meeting", updatedTask.taskName)
        assertEquals(Priority.MEDIUM, updatedTask.priority)
        
        // 5. Test search functionality
        val searchResults = taskService.searchTasks(
            taskName = "code",
            priority = null,
            taskType = null,
            startDate = null,
            endDate = null
        )
        assertEquals(1, searchResults.size)
        assertEquals("Code Review", searchResults[0].taskName)
        
        // 6. Test filtering by priority
        val highPriorityTasks = taskService.getTasksByPriority(Priority.HIGH)
        assertEquals(0, highPriorityTasks.size) // Task 1 was updated to MEDIUM
        
        val mediumPriorityTasks = taskService.getTasksByPriority(Priority.MEDIUM)
        assertEquals(2, mediumPriorityTasks.size) // Task 1 (updated) and Task 3
        
        // 7. Test filtering by task type
        val workTasks = taskService.getTasksByType(TaskType.WORK)
        assertEquals(2, workTasks.size)
        
        val homeTasks = taskService.getTasksByType(TaskType.HOME)
        assertEquals(1, homeTasks.size)
        
        // 8. Test deadline filtering
        val tasksWithDeadline = taskService.getTasksByDeadline(true)
        assertEquals(2, tasksWithDeadline.size)
        
        val tasksWithoutDeadline = taskService.getTasksByDeadline(false)
        assertEquals(1, tasksWithoutDeadline.size)
        assertEquals("Home Cleaning", tasksWithoutDeadline[0].taskName)
        
        // 9. Test ordering
        val tasksByDate = taskService.getTasksOrderedByDate()
        assertEquals(3, tasksByDate.size)
        // Should be ordered by date desc
        assertEquals(LocalDate.of(2024, 1, 17), tasksByDate[0].date)
        assertEquals(LocalDate.of(2024, 1, 16), tasksByDate[1].date)
        assertEquals(LocalDate.of(2024, 1, 15), tasksByDate[2].date)
        
        val tasksByDeadline = taskService.getTasksOrderedByDeadline()
        assertEquals(3, tasksByDeadline.size)
        // Task without deadline should come first
        assertEquals("Home Cleaning", tasksByDeadline[0].taskName)
        
        // 10. Test task deletion
        taskService.deleteTask(createdTask2.id)
        
        val remainingTasks = taskService.getAllTasks()
        assertEquals(2, remainingTasks.size)
        
        // Verify deleted task is not found
        try {
            taskService.getTaskById(createdTask2.id)
            assertTrue(false, "Should have thrown exception for deleted task")
        } catch (e: IllegalArgumentException) {
            assertEquals("Task not found with id: ${createdTask2.id}", e.message)
        }
    }
    
    @Test
    fun `complex search scenarios should work correctly`() {
        // Create diverse test data
        val tasks = listOf(
            TaskRequest(
                date = LocalDate.of(2024, 1, 10),
                taskName = "Important Meeting",
                comment = "Q1 planning",
                deadline = LocalDateTime.of(2024, 1, 15, 14, 0),
                priority = Priority.HIGH,
                taskType = TaskType.WORK
            ),
            TaskRequest(
                date = LocalDate.of(2024, 1, 12),
                taskName = "Doctor Appointment",
                comment = "Annual checkup",
                deadline = LocalDateTime.of(2024, 1, 12, 10, 0),
                priority = Priority.HIGH,
                taskType = TaskType.HOME
            ),
            TaskRequest(
                date = LocalDate.of(2024, 1, 15),
                taskName = "Team Meeting",
                comment = "Sprint planning",
                deadline = LocalDateTime.of(2024, 1, 15, 9, 0),
                priority = Priority.MEDIUM,
                taskType = TaskType.WORK
            ),
            TaskRequest(
                date = LocalDate.of(2024, 1, 20),
                taskName = "Grocery Shopping",
                comment = "Weekly shopping",
                deadline = null,
                priority = Priority.LOW,
                taskType = TaskType.HOME
            ),
            TaskRequest(
                date = LocalDate.of(2024, 1, 25),
                taskName = "Important Project Review",
                comment = "Final review",
                deadline = LocalDateTime.of(2024, 1, 26, 16, 0),
                priority = Priority.HIGH,
                taskType = TaskType.WORK
            )
        )
        
        val createdTasks = tasks.map { taskService.createTask(it) }
        
        // Test complex search combinations
        
        // 1. Search by task name containing "meeting"
        val meetingTasks = taskService.searchTasks(
            taskName = "meeting",
            priority = null,
            taskType = null,
            startDate = null,
            endDate = null
        )
        assertEquals(3, meetingTasks.size)
        
        // 2. Search by priority and task type
        val highPriorityWorkTasks = taskService.searchTasks(
            taskName = null,
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            startDate = null,
            endDate = null
        )
        assertEquals(2, highPriorityWorkTasks.size)
        
        // 3. Search by date range
        val midJanuaryTasks = taskService.searchTasks(
            taskName = null,
            priority = null,
            taskType = null,
            startDate = LocalDate.of(2024, 1, 12),
            endDate = LocalDate.of(2024, 1, 18)
        )
        assertEquals(2, midJanuaryTasks.size)
        
        // 4. Complex multi-criteria search
        val complexSearch = taskService.searchTasks(
            taskName = "important",
            priority = Priority.HIGH,
            taskType = TaskType.WORK,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 31)
        )
        assertEquals(2, complexSearch.size)
        
        // 5. Search with no results
        val noResults = taskService.searchTasks(
            taskName = "nonexistent",
            priority = null,
            taskType = null,
            startDate = null,
            endDate = null
        )
        assertEquals(0, noResults.size)
        
        // 6. Search with very restrictive criteria
        val veryRestrictive = taskService.searchTasks(
            taskName = "grocery",
            priority = Priority.LOW,
            taskType = TaskType.HOME,
            startDate = LocalDate.of(2024, 1, 19),
            endDate = LocalDate.of(2024, 1, 21)
        )
        assertEquals(1, veryRestrictive.size)
        assertEquals("Grocery Shopping", veryRestrictive[0].taskName)
    }
    
    @Test
    fun `data integrity should be maintained throughout operations`() {
        // Create initial task
        val originalTask = TaskRequest(
            date = LocalDate.of(2024, 1, 15),
            taskName = "Original Task",
            comment = "Original comment",
            deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
            priority = Priority.HIGH,
            taskType = TaskType.WORK
        )
        
        val created = taskService.createTask(originalTask)
        val originalCreatedAt = created.createdAt
        
        // Update the task
        val updatedTask = originalTask.copy(
            taskName = "Updated Task",
            priority = Priority.LOW
        )
        
        val updated = taskService.updateTask(created.id, updatedTask)
        
        // Verify data integrity
        assertEquals(created.id, updated.id) // ID should remain the same
        assertEquals(originalCreatedAt, updated.createdAt) // createdAt should not change
        assertTrue(updated.updatedAt.isAfter(originalCreatedAt)) // updatedAt should be updated
        assertEquals("Updated Task", updated.taskName)
        assertEquals(Priority.LOW, updated.priority)
        assertEquals(originalTask.date, updated.date) // Date should be updated
        assertEquals(originalTask.comment, updated.comment) // Comment should be updated
        assertEquals(originalTask.deadline, updated.deadline) // Deadline should be updated
        assertEquals(originalTask.taskType, updated.taskType) // Type should be updated
        
        // Verify task still exists in repository
        val retrieved = taskService.getTaskById(created.id)
        assertEquals(updated.taskName, retrieved.taskName)
        assertEquals(updated.priority, retrieved.priority)
    }
    
    @Test
    fun `edge cases should be handled correctly`() {
        // Test with task having very long name
        val longNameTask = TaskRequest(
            date = LocalDate.of(2024, 1, 15),
            taskName = "A".repeat(1000), // Very long task name
            comment = "Test comment",
            deadline = LocalDateTime.of(2024, 1, 20, 17, 0),
            priority = Priority.HIGH,
            taskType = TaskType.WORK
        )
        
        val created = taskService.createTask(longNameTask)
        assertNotNull(created.id)
        assertEquals("A".repeat(1000), created.taskName)
        
        // Test with empty comment
        val emptyCommentTask = TaskRequest(
            date = LocalDate.of(2024, 1, 15),
            taskName = "Task with empty comment",
            comment = "",
            deadline = null,
            priority = Priority.MEDIUM,
            taskType = TaskType.HOME
        )
        
        val createdEmptyComment = taskService.createTask(emptyCommentTask)
        assertEquals("", createdEmptyComment.comment)
        
        // Test with past deadline
        val pastDeadlineTask = TaskRequest(
            date = LocalDate.of(2024, 1, 15),
            taskName = "Past deadline task",
            comment = "This task has a past deadline",
            deadline = LocalDateTime.of(2020, 1, 1, 12, 0), // Past deadline
            priority = Priority.LOW,
            taskType = TaskType.WORK
        )
        
        val createdPastDeadline = taskService.createTask(pastDeadlineTask)
        assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0), createdPastDeadline.deadline)
        
        // Test search with empty string
        val emptyStringSearch = taskService.searchTasks(
            taskName = "",
            priority = null,
            taskType = null,
            startDate = null,
            endDate = null
        )
        assertEquals(3, emptyStringSearch.size) // Should return all tasks
        
        // Test ordering with mixed deadline values
        val tasksOrderedByDeadline = taskService.getTasksOrderedByDeadline()
        assertEquals(3, tasksOrderedByDeadline.size)
        // Task with null deadline comes first, then ordered by deadline
        assertEquals("Task with empty comment", tasksOrderedByDeadline[0].taskName)
    }
    
    @Test
    fun `performance test with multiple operations`() {
        // Create a larger dataset for performance testing
        val tasks = (1..100).map { index ->
            TaskRequest(
                date = LocalDate.of(2024, 1, 1).plusDays(index.toLong()),
                taskName = "Task $index",
                comment = "Comment for task $index",
                deadline = if (index % 2 == 0) LocalDateTime.of(2024, 1, 1, 12, 0).plusDays(index.toLong()) else null,
                priority = Priority.values()[index % 3],
                taskType = TaskType.values()[index % 2]
            )
        }
        
        // Measure creation time
        val startTime = System.currentTimeMillis()
        val createdTasks = tasks.map { taskService.createTask(it) }
        val creationTime = System.currentTimeMillis() - startTime
        
        // Verify all tasks were created
        assertEquals(100, createdTasks.size)
        
        // Test bulk operations
        val allTasks = taskService.getAllTasks()
        assertEquals(100, allTasks.size)
        
        // Test search performance with large dataset
        val searchStartTime = System.currentTimeMillis()
        val searchResults = taskService.searchTasks(
            taskName = "Task",
            priority = null,
            taskType = null,
            startDate = null,
            endDate = null
        )
        val searchTime = System.currentTimeMillis() - searchStartTime
        
        assertEquals(100, searchResults.size) // All tasks contain "Task"
        
        // Test filtering performance
        val filterStartTime = System.currentTimeMillis()
        val highPriorityTasks = taskService.getTasksByPriority(Priority.HIGH)
        val filterTime = System.currentTimeMillis() - filterStartTime
        
        // Should be approximately 33 tasks (every 3rd task is HIGH priority)
        assertTrue(highPriorityTasks.size >= 30 && highPriorityTasks.size <= 35)
        
        // Test ordering performance
        val orderStartTime = System.currentTimeMillis()
        val orderedTasks = taskService.getTasksOrderedByDate()
        val orderTime = System.currentTimeMillis() - orderStartTime
        
        assertEquals(100, orderedTasks.size)
        
        // Basic performance assertions (these are rough estimates)
        assertTrue(creationTime < 5000, "Creation should complete in under 5 seconds")
        assertTrue(searchTime < 1000, "Search should complete in under 1 second")
        assertTrue(filterTime < 1000, "Filtering should complete in under 1 second")
        assertTrue(orderTime < 1000, "Ordering should complete in under 1 second")
    }
}