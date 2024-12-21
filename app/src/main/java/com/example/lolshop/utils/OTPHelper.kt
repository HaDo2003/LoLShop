package com.example.lolshop.utils

import android.util.Log
import papaya.`in`.sendmail.SendMail

class OTPHelper {
    fun verifyOtp(otp: String, random: String): Boolean{
        Log.d("OTP random", otp)
        return otp == random

    }

    fun generateOtp(email: String, purpose: String): String {
        val randomOtp = (100000..999999).random() // Generates a random OTP with 6 digits
        val subject = "Your OTP Code for LoL Shop Verification"
        val body = """
        Hi, there

        We received a request to $purpose. 
        Please use the following OTP to complete your verification:
        
        **$randomOtp**

        This OTP is valid for 5 minutes. Do not share it with anyone.
        
        Regards,
        LoL Shop Team
    """.trimIndent()

        val mail = SendMail(
            "hadotaydo20@gmail.com",
            "zjkilymrscoyepyk",
            email,
            subject,
            body
        )

        mail.execute()
        Log.d("OTP random", "$randomOtp")
        return randomOtp.toString()
    }

    fun resendOTP(email: String): String{
        val newOtp = generateOtp(email, "resend OTP")
        return newOtp
    }
}