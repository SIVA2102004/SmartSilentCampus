package com.smartsilentcampus.presentation.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsilentcampus.domain.model.SoundProfile
import com.smartsilentcampus.domain.model.SupportedRingerMode
import com.smartsilentcampus.domain.repository.SoundProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val soundProfileRepository: SoundProfileRepository
) : ViewModel() {

    val profiles: StateFlow<List<SoundProfile>> = soundProfileRepository.getAllProfilesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createCustomProfile(
        name: String,
        ringerMode: SupportedRingerMode,
        vibrate: Boolean,
        ringVol: Int,
        mediaVol: Int
    ) {
        viewModelScope.launch {
            val profile = SoundProfile(
                id = UUID.randomUUID().toString(),
                name = name,
                ringerMode = ringerMode,
                ringVolumePercent = ringVol,
                mediaVolumePercent = mediaVol,
                isVibrationEnabled = vibrate,
                isDefault = false
            )
            soundProfileRepository.saveProfile(profile)
        }
    }
}
