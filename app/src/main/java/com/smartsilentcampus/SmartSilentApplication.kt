package com.smartsilentcampus

import android.app.Application
import com.smartsilentcampus.domain.repository.SoundProfileRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SmartSilentApplication : Application() {

    @Inject
    lateinit var soundProfileRepository: SoundProfileRepository

    override fun onCreate() {
        super.onCreate()
        // Initialize default sound profiles if database is fresh
        CoroutineScope(Dispatchers.IO).launch {
            try {
                soundProfileRepository.seedDefaultProfilesIfEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
