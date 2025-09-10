package com.ezycart.presentation.activation

import com.ezycart.domain.model.AppMode

data class ActivationState(
    val activationCode: String = "",
    val trolleyNumber: String = "",
    val appMode: AppMode = AppMode.EzyCartPicker,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isActivationSuccessful: Boolean = false
)