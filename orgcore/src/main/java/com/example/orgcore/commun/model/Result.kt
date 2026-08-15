package com.example.orgcore.commun.model

sealed class Result<out S,out E> {


    data class Success<out T>(val data: T): Result<T,Nothing>()

    data class Error<out T>(val throwable: Throwable? = null,val error: T): Result<Nothing,T>()
}