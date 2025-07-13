package com.superorganizer.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "tasks")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @NotNull
    @Column(nullable = false)
    val date: LocalDate,
    
    @NotBlank
    @Column(nullable = false)
    val taskName: String,
    
    @Column(columnDefinition = "TEXT")
    val comment: String? = null,
    
    @Column
    val deadline: LocalDateTime? = null,
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val priority: Priority,
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val taskType: TaskType,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)