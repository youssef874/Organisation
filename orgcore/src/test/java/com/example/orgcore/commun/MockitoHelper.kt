package com.example.orgcore.commun

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers

fun <T> anyObject(): T {
    ArgumentMatchers.any<T>()
    return castNull()
}

fun <T> capture(captor: ArgumentCaptor<T>): T {
    captor.capture()
    return castNull()
}

@Suppress("UNCHECKED_CAST")
fun <T> castNull(): T = null as T
