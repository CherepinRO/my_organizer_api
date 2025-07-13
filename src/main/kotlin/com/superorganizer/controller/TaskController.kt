package com.superorganizer.controller

import com.superorganizer.dto.TaskRequest
import com.superorganizer.dto.TaskResponse
import com.superorganizer.model.Priority
import com.superorganizer.model.TaskType
import com.superorganizer.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "API for managing tasks in Super Organizer")
class TaskController(private val taskService: TaskService) {
    
    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task with the provided details")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Task created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request body")
    ])
    fun createTask(@Valid @RequestBody taskRequest: TaskRequest): ResponseEntity<TaskResponse> {
        val createdTask = taskService.createTask(taskRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask)
    }
    
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks from the system")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    fun getAllTasks(): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getAllTasks()
        return ResponseEntity.ok(tasks)
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Task found successfully"),
        ApiResponse(responseCode = "404", description = "Task not found")
    ])
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskResponse> {
        val task = taskService.getTaskById(id)
        return ResponseEntity.ok(task)
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates an existing task with new details")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Task updated successfully"),
        ApiResponse(responseCode = "404", description = "Task not found"),
        ApiResponse(responseCode = "400", description = "Invalid request body")
    ])
    fun updateTask(
        @PathVariable id: Long,
        @Valid @RequestBody taskRequest: TaskRequest
    ): ResponseEntity<TaskResponse> {
        val updatedTask = taskService.updateTask(id, taskRequest)
        return ResponseEntity.ok(updatedTask)
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task by its ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        ApiResponse(responseCode = "404", description = "Task not found")
    ])
    fun deleteTask(@PathVariable id: Long): ResponseEntity<Void> {
        taskService.deleteTask(id)
        return ResponseEntity.noContent().build()
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search tasks", description = "Search tasks by various criteria")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    fun searchTasks(
        @RequestParam(required = false) 
        @Parameter(description = "Task name to search for (case-insensitive)")
        taskName: String?,
        
        @RequestParam(required = false)
        @Parameter(description = "Priority level (HIGH, MEDIUM, LOW)")
        priority: Priority?,
        
        @RequestParam(required = false)
        @Parameter(description = "Task type (WORK, HOME)")
        taskType: TaskType?,
        
        @RequestParam(required = false)
        @Parameter(description = "Start date for date range search")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,
        
        @RequestParam(required = false)
        @Parameter(description = "End date for date range search")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?
    ): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.searchTasks(taskName, priority, taskType, startDate, endDate)
        return ResponseEntity.ok(tasks)
    }
    
    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority", description = "Retrieves all tasks with a specific priority")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    fun getTasksByPriority(@PathVariable priority: Priority): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getTasksByPriority(priority)
        return ResponseEntity.ok(tasks)
    }
    
    @GetMapping("/type/{taskType}")
    @Operation(summary = "Get tasks by type", description = "Retrieves all tasks of a specific type")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    fun getTasksByType(@PathVariable taskType: TaskType): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getTasksByType(taskType)
        return ResponseEntity.ok(tasks)
    }
    
    @GetMapping("/deadline")
    @Operation(summary = "Get tasks by deadline status", description = "Retrieves tasks with or without deadlines")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    fun getTasksByDeadline(
        @RequestParam(defaultValue = "true")
        @Parameter(description = "True to get tasks with deadlines, false to get tasks without deadlines")
        hasDeadline: Boolean
    ): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getTasksByDeadline(hasDeadline)
        return ResponseEntity.ok(tasks)
    }
    
    @GetMapping("/ordered/deadline")
    @Operation(summary = "Get tasks ordered by deadline", description = "Retrieves all tasks ordered by deadline (ascending)")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    fun getTasksOrderedByDeadline(): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getTasksOrderedByDeadline()
        return ResponseEntity.ok(tasks)
    }
    
    @GetMapping("/ordered/priority")
    @Operation(summary = "Get tasks ordered by priority", description = "Retrieves all tasks ordered by priority (HIGH to LOW)")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    fun getTasksOrderedByPriority(): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getTasksOrderedByPriority()
        return ResponseEntity.ok(tasks)
    }
    
    @GetMapping("/ordered/date")
    @Operation(summary = "Get tasks ordered by date", description = "Retrieves all tasks ordered by date (newest first)")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    fun getTasksOrderedByDate(): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getTasksOrderedByDate()
        return ResponseEntity.ok(tasks)
    }
}