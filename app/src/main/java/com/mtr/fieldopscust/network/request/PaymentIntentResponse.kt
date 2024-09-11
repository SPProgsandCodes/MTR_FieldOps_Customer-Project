package com.mtr.fieldopscust.network.request

class PaymentIntentResponse (
    val result: PaymentInfo,
    val isSuccess: Boolean,
    val message: String
)

data class PaymentInfo(
    val intentId: String,
    val paymentIntent: String,
    val ephemeralKey: String,
    val customer: String,
    val publishableKey: String
)