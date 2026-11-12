package com.example.orgcore.internal.commun

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.attendance.model.Attendance
import java.util.Calendar
import java.util.TimeZone

sealed class FakeData {

    companion object{
        private val worker1 = Account(
            id = "10",
            name = "WORKER",
            email = "worker@gmail.com",
            phoneNumber = "12345678",
            image = null,
            type = AccountUserType.WORKER
        )

        private val worker2 = Account(
            id = "20",
            name = "WORKER2",
            email = "worker2@gmail.com",
            phoneNumber = "12345670",
            image = null,
            type = AccountUserType.WORKER
        )

        private val worker3 = Account(
            id = "30",
            name = "WORKER3",
            email = "worker3@gmail.com",
            phoneNumber = "12345671",
            image = null,
            type = AccountUserType.WORKER
        )

        private val manager = Account(
            id = "40",
            name = "MANAGER",
            email = "manager@gmail.com",
            phoneNumber = "25836911",
            image = null,
            type = AccountUserType.MANAGER
        )

        private val boss = Account(
            id = "50",
            name = "BOSS",
            email = "boss@gmail.com",
            phoneNumber = "0000000",
            image = null,
            type = AccountUserType.WORKER
        )

        fun getFakeData(@AccountUserType type: Int)= when(type){
            AccountUserType.WORKER -> Employee
            AccountUserType.MANAGER -> Manager
            else -> Boss
        }

        fun dateToMillis(
            year: Int,
            month: Int,
            day: Int,
            timeZone: TimeZone = TimeZone.getDefault()
        ): Long {
            val calendar = Calendar.getInstance(timeZone)
            calendar.set(year, month - 1, day, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
    }

    abstract val account: Account

    abstract val relatedAccount: List<Account>

    abstract val attendance: List<Attendance>

    data object Employee: FakeData() {
        override val account: Account
            get() = Account(
                id = "3",
                name = "Worker",
                phoneNumber = "345678",
                email = "worker@example.com",
                image = null,
                type = AccountUserType.WORKER
            )
        override val relatedAccount: List<Account>
            get() = listOf(worker1, manager, boss)
        override val attendance: List<Attendance>
            get() = listOf(
                Attendance(
                    id = "att_1",
                    serverAccountId = "3",
                    checkIn = 1000L,
                    checkOut = 5000L,
                    date = dateToMillis(2026, 11, 10),
                    attendanceStatus = AttendanceStatus.PRESENT,
                    isApproved = true
                ),
                Attendance(
                    id = "att_2",
                    serverAccountId = "3",
                    checkIn = 0L,
                    checkOut = 0L,
                    date = dateToMillis(2026, 11, 9),
                    attendanceStatus = AttendanceStatus.PRESENT,
                    isApproved = true
                ),
                Attendance(
                    id = "att_5",
                    serverAccountId = "3",
                    checkIn = 0L,
                    checkOut = 0L,
                    date = dateToMillis(2026, 11, 18),
                    attendanceStatus = AttendanceStatus.PENDING,
                    isApproved = true
                )
            )
    }

    data object Manager: FakeData() {
        override val account: Account
            get() = Account(
                id = "2",
                name = "Manager",
                phoneNumber = "234567",
                email = "manager@example.com",
                image = null,
                type = AccountUserType.MANAGER
            )
        override val relatedAccount: List<Account>
            get() = listOf(worker1, worker2, boss)
        override val attendance: List<Attendance>
            get() = listOf(
                Attendance(
                    id = "att_3",
                    serverAccountId = "2",
                    checkIn = 1000L,
                    checkOut = 5000L,
                    date = 300000L,
                    attendanceStatus = AttendanceStatus.PRESENT,
                    isApproved = true
                )
            )
    }

    data object Boss: FakeData() {
        override val account: Account
            get() = Account(
                id = "1",
                name = "Admin",
                phoneNumber = "123456",
                email = "admin@example.com",
                image = null,
                type = AccountUserType.BOSS
            )
        override val relatedAccount: List<Account>
            get() = listOf(worker1, worker2, worker3, boss, manager)
        override val attendance: List<Attendance>
            get() = listOf(
                Attendance(
                    id = "att_4",
                    serverAccountId = "1",
                    checkIn = 1000L,
                    checkOut = 5000L,
                    date = 400000L,
                    attendanceStatus = AttendanceStatus.PRESENT,
                    isApproved = true
                )
            )
    }
}
