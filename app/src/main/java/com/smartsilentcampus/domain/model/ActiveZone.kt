package com.smartsilentcampus.domain.model

data class ActiveZone(
    val locationId: String,
    val enteredAt: Long = System.currentTimeMillis(),
    val priority: Int,
    val profileId: String
)
