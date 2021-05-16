package com.example.contact.model

import android.graphics.Bitmap

data class Contacts (
    val id: String = "",
    val name: String ="",
    val phoneNumber: String ="",
    val photo : Bitmap? = null
)
