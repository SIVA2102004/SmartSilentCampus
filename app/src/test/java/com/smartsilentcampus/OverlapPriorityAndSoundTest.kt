package com.smartsilentcampus

import com.smartsilentcampus.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class OverlapPriorityAndSoundTest {

    @Test
    fun testDefaultProfilesContainSilentNormalAndExam() {
        val defaults = SoundProfile.defaultProfiles()
        assertTrue(defaults.any { it.id == SoundProfile.DEFAULT_SILENT_ID })
        assertTrue(defaults.any { it.id == SoundProfile.DEFAULT_NORMAL_ID })
        assertTrue(defaults.any { it.id == SoundProfile.DEFAULT_EXAM_ID })
    }

    @Test
    fun testLocationTypePriorityOrdering() {
        assertTrue(LocationType.EXAM_HALL.defaultPriority > LocationType.CLASSROOM.defaultPriority)
        assertTrue(LocationType.CLASSROOM.defaultPriority > LocationType.LIBRARY.defaultPriority)
        assertTrue(LocationType.LIBRARY.defaultPriority > LocationType.COLLEGE.defaultPriority)
        assertTrue(LocationType.COLLEGE.defaultPriority > LocationType.CUSTOM.defaultPriority)
    }

    @Test
    fun testActiveZoneHighestPrioritySelection() {
        val zones = listOf(
            ActiveZone(locationId = "campus_1", priority = 30, profileId = SoundProfile.DEFAULT_SILENT_ID),
            ActiveZone(locationId = "classroom_1", priority = 80, profileId = SoundProfile.DEFAULT_SILENT_ID),
            ActiveZone(locationId = "exam_hall_1", priority = 100, profileId = SoundProfile.DEFAULT_EXAM_ID)
        )

        val highest = zones.maxByOrNull { it.priority }
        assertNotNull(highest)
        assertEquals("exam_hall_1", highest?.locationId)
        assertEquals(100, highest?.priority)
    }

    @Test
    fun testPreviousSoundStatePreservation() {
        val captured = PreviousSoundState(
            id = 1L,
            ringerMode = 2, // NORMAL
            ringVolume = 8,
            notificationVolume = 6,
            mediaVolume = 10,
            alarmVolume = 12,
            systemVolume = 5,
            vibrationSetting = 0,
            capturedAt = 100000L,
            isActive = true
        )

        assertEquals(8, captured.ringVolume)
        assertEquals(2, captured.ringerMode)
        assertTrue(captured.isActive)
    }
}
