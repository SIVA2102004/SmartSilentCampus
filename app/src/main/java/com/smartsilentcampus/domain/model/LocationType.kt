package com.smartsilentcampus.domain.model

enum class LocationType(val displayName: String, val defaultPriority: Int) {
    EXAM_HALL("Examination Hall", 100),
    CLASSROOM("Classroom", 80),
    LABORATORY("Laboratory", 70),
    LIBRARY("Library", 60),
    SEMINAR_HALL("Seminar Hall", 50),
    COLLEGE("College Campus", 30),
    HOSTEL("Hostel", 20),
    CUSTOM("Custom Location", 10)
}
