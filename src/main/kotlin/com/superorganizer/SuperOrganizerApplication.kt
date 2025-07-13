package com.superorganizer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SuperOrganizerApplication

fun main(args: Array<String>) {
    runApplication<SuperOrganizerApplication>(*args)
}