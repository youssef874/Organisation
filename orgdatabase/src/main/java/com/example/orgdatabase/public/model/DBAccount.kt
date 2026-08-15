package com.example.orgdatabase.public.model

import androidx.annotation.IntDef

data class DBAccount(
    val id: Long = 0,
    val name: String,
    val photoUri: String? = null,
    val isUser: Boolean,
    val accountType: @AccountType Int, // 0: Boss, 1: Manager, 2: Employee
    val serverId: String?,
    val position: String? = null,
    val supervisorId: Long? = null,
    val emails: List<String> = emptyList(),
    val phoneNumbers: List<String> = emptyList()
)

@IntDef(
    AccountType.BOSS,
    AccountType.MANAGER,
    AccountType.EMPLOYEE
)

@Target(AnnotationTarget.TYPE)
annotation class AccountType{
    companion object{
        const val BOSS = 0
        const val MANAGER = 1
        const val EMPLOYEE = 2
    }
}
