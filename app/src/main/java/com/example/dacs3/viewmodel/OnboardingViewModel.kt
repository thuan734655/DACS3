package com.example.dacs3.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.dacs3.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel() 