package com.organisation.domaine

import com.organisation.domaine.account.repo.AccountRepository
import com.organisation.domaine.account.repo.DefaultAccountRepo
import com.organisation.domaine.attendance.AttendanceRepository
import com.organisation.domaine.attendance.FakeAttendanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepoProvider{

    @Provides
    fun getAccountRepo(): AccountRepository = DefaultAccountRepo

    @Provides
    fun getAttendanceRepo(): AttendanceRepository = FakeAttendanceRepository()
}