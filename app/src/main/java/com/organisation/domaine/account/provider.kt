package com.organisation.domaine.account

import com.organisation.domaine.account.repo.AccountRepository
import com.organisation.domaine.account.repo.DefaultAccountRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepoProvider{

    @Provides
    fun getAccountRepo(): AccountRepository = DefaultAccountRepo
}