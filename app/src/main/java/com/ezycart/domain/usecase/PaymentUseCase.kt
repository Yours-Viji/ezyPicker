package com.ezycart.domain.usecase

import com.ezycart.data.remote.dto.CreateJwtTokenRequest
import com.ezycart.data.remote.dto.JwtTokenResponse
import com.ezycart.data.remote.dto.NearPaymentSessionResponse
import com.ezycart.data.remote.dto.NetworkResponse
import com.ezycart.data.remote.dto.ShoppingCartDetails
import com.ezycart.domain.repository.AuthRepository

import javax.inject.Inject

class PaymentUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend  fun invoke(url: String,
                        jwtTokenRequest: CreateJwtTokenRequest): NetworkResponse<JwtTokenResponse> {
        return authRepository.createJwtToken(url,jwtTokenRequest)
    }

    suspend  fun createNewJwtToken(url: String,
                                   jwtTokenRequest: CreateJwtTokenRequest): NetworkResponse<JwtTokenResponse> {
        return authRepository.createJwtToken(url,jwtTokenRequest)
    }

    suspend  fun createNearPaySession(url: String): NetworkResponse<NearPaymentSessionResponse> {
        return authRepository.createNearPaySession(url)
    }
}