package com.superorganizer.repository

import com.superorganizer.model.Priority
import com.superorganizer.model.Task
import com.superorganizer.model.TaskType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    
    fun findByTaskNameContainingIgnoreCase(taskName: String): List<Task>
    
    fun findByPriority(priority: Priority): List<Task>
    
    fun findByTaskType(taskType: TaskType): List<Task>
    
    fun findByDateBetween(startDate: LocalDate, endDate: LocalDate): List<Task>
    
    fun findByDeadlineBetween(startDeadline: LocalDateTime, endDeadline: LocalDateTime): List<Task>
    
    fun findByDeadlineIsNull(): List<Task>
    
    fun findByDeadlineIsNotNull(): List<Task>
    
    @Query("SELECT t FROM Task t WHERE " +
           "(:taskName IS NULL OR LOWER(t.taskName) LIKE LOWER(CONCAT('%', :taskName, '%'))) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:taskType IS NULL OR t.taskType = :taskType) AND " +
           "(:startDate IS NULL OR t.date >= :startDate) AND " +
           "(:endDate IS NULL OR t.date <= :endDate)")
    fun findTasksByFilters(
        @Param("taskName") taskName: String?,
        @Param("priority") priority: Priority?,
        @Param("taskType") taskType: TaskType?,
        @Param("startDate") startDate: LocalDate?,
        @Param("endDate") endDate: LocalDate?
    ): List<Task>
    
    fun findByOrderByDeadlineAsc(): List<Task>
    
    fun findByOrderByPriorityAsc(): List<Task>
    
    fun findByOrderByDateDesc(): List<Task>
}