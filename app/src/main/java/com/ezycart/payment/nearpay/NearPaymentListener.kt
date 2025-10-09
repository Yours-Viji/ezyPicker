package com.ezycart.payment.nearpay

import io.nearpay.sdk.utils.enums.TransactionData

interface NearPaymentListener {
    fun onPaymentSuccess(transactionData: TransactionData)
    fun onPaymentFailed(error: String)
}