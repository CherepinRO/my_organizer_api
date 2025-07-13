package com.superorganizer.dto

import com.superorganizer.model.Priority
import com.superorganizer.model.TaskType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskRequest(
    @NotNull(message = "Date is required")
    val date: LocalDate,
    
    @NotBlank(message = "Task name is required")
    val taskName: String,
    
    val comment: String? = null,
    
    val deadline: LocalDateTime? = null,
    
    @NotNull(message = "Priority is required")
    val priority: Priority,
    
    @NotNull(message = "Task type is required")
    val taskType: TaskType
)