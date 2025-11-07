package com.example.sagalyze.app

data class Patient(
    val id: Int,
    val name: String,
    val age: Int,
    val gender: String,
    val condition: String,
    val bloodGroup: String
)