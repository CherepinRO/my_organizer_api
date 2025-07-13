package com.superorganizer.dto

import com.superorganizer.model.Priority
import com.superorganizer.model.Task
import com.superorganizer.model.TaskType
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskResponse(
    val id: Long,
    val date: LocalDate,
    val taskName: String,
    val comment: String?,
    val deadline: LocalDateTime?,
    val priority: Priority,
    val taskType: TaskType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(task: Task): TaskResponse {
            return TaskResponse(
                id = task.id!!,
                date = task.date,
                taskName = task.taskName,
                comment = task.comment,
                deadline = task.deadline,
                priority = task.priority,
                taskType = task.taskType,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt
            )
        }
    }
}