package com.superorganizer.config

import com.superorganizer.model.Priority
import com.superorganizer.model.Task
import com.superorganizer.model.TaskType
import com.superorganizer.repository.TaskRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DataInitializer(private val taskRepository: TaskRepository) : CommandLineRunner {
    
    override fun run(vararg args: String?) {
        if (taskRepository.count() == 0L) {
            createSampleTasks()
        }
    }
    
    private fun createSampleTasks() {
        val sampleTasks = listOf(
            Task(
                date = LocalDate.now(),
                taskName = "Complete project documentation",
                comment = "Write comprehensive documentation for the Super Organizer API",
                deadline = LocalDateTime.now().plusDays(3),
                priority = Priority.HIGH,
                taskType = TaskType.WORK
            ),
            Task(
                date = LocalDate.now().minusDays(1),
                taskName = "Grocery shopping",
                comment = "Buy vegetables, fruits, and dairy products",
                deadline = LocalDateTime.now().plusDays(1),
                priority = Priority.MEDIUM,
                taskType = TaskType.HOME
            ),
            Task(
                date = LocalDate.now().minusDays(2),
                taskName = "Code review",
                comment = "Review pull requests from team members",
                deadline = null,
                priority = Priority.MEDIUM,
                taskType = TaskType.WORK
            ),
            Task(
                date = LocalDate.now(),
                taskName = "Clean the house",
                comment = "Deep cleaning of all rooms",
                deadline = LocalDateTime.now().plusDays(2),
                priority = Priority.LOW,
                taskType = TaskType.HOME
            ),
            Task(
                date = LocalDate.now().plusDays(1),
                taskName = "Prepare presentation",
                comment = "Create slides for the quarterly meeting",
                deadline = LocalDateTime.now().plusDays(5),
                priority = Priority.HIGH,
                taskType = TaskType.WORK
            ),
            Task(
                date = LocalDate.now().minusDays(3),
                taskName = "Exercise routine",
                comment = "30-minute workout session",
                deadline = null,
                priority = Priority.MEDIUM,
                taskType = TaskType.HOME
            )
        )
        
        taskRepository.saveAll(sampleTasks)
        println("Sample tasks created successfully!")
    }
}