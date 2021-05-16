package com.example.contact.utils

import androidx.fragment.app.Fragment

interface IAdapter<T> {
    fun onSend(obj: T, fragment: Fragment)
}