package com.superorganizer.service

import com.superorganizer.dto.TaskRequest
import com.superorganizer.dto.TaskResponse
import com.superorganizer.model.Priority
import com.superorganizer.model.Task
import com.superorganizer.model.TaskType
import com.superorganizer.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TaskService(private val taskRepository: TaskRepository) {
    
    fun createTask(taskRequest: TaskRequest): TaskResponse {
        val task = Task(
            date = taskRequest.date,
            taskName = taskRequest.taskName,
            comment = taskRequest.comment,
            deadline = taskRequest.deadline,
            priority = taskRequest.priority,
            taskType = taskRequest.taskType
        )
        
        val savedTask = taskRepository.save(task)
        return TaskResponse.from(savedTask)
    }
    
    fun getAllTasks(): List<TaskResponse> {
        return taskRepository.findAll().map { TaskResponse.from(it) }
    }
    
    fun getTaskById(id: Long): TaskResponse {
        val task = taskRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Task not found with id: $id") }
        return TaskResponse.from(task)
    }
    
    fun updateTask(id: Long, taskRequest: TaskRequest): TaskResponse {
        val existingTask = taskRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Task not found with id: $id") }
        
        val updatedTask = existingTask.copy(
            date = taskRequest.date,
            taskName = taskRequest.taskName,
            comment = taskRequest.comment,
            deadline = taskRequest.deadline,
            priority = taskRequest.priority,
            taskType = taskRequest.taskType,
            updatedAt = LocalDateTime.now()
        )
        
        val savedTask = taskRepository.save(updatedTask)
        return TaskResponse.from(savedTask)
    }
    
    fun deleteTask(id: Long) {
        if (!taskRepository.existsById(id)) {
            throw IllegalArgumentException("Task not found with id: $id")
        }
        taskRepository.deleteById(id)
    }
    
    fun searchTasks(
        taskName: String? = null,
        priority: Priority? = null,
        taskType: TaskType? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): List<TaskResponse> {
        return taskRepository.findTasksByFilters(taskName, priority, taskType, startDate, endDate)
            .map { TaskResponse.from(it) }
    }
    
    fun getTasksByPriority(priority: Priority): List<TaskResponse> {
        return taskRepository.findByPriority(priority)
            .map { TaskResponse.from(it) }
    }
    
    fun getTasksByType(taskType: TaskType): List<TaskResponse> {
        return taskRepository.findByTaskType(taskType)
            .map { TaskResponse.from(it) }
    }
    
    fun getTasksByDeadline(hasDeadline: Boolean): List<TaskResponse> {
        return if (hasDeadline) {
            taskRepository.findByDeadlineIsNotNull()
        } else {
            taskRepository.findByDeadlineIsNull()
        }.map { TaskResponse.from(it) }
    }
    
    fun getTasksOrderedByDeadline(): List<TaskResponse> {
        return taskRepository.findByOrderByDeadlineAsc()
            .map { TaskResponse.from(it) }
    }
    
    fun getTasksOrderedByPriority(): List<TaskResponse> {
        return taskRepository.findByOrderByPriorityAsc()
            .map { TaskResponse.from(it) }
    }
    
    fun getTasksOrderedByDate(): List<TaskResponse> {
        return taskRepository.findByOrderByDateDesc()
            .map { TaskResponse.from(it) }
    }
}