package com.almagribii.goalmate.di

import com.almagribii.goalmate.data.repository.AuthRepositoryImpl
import com.almagribii.goalmate.data.repository.GoalRepositoryImpl
import com.almagribii.goalmate.domain.repository.AuthRepository
import com.almagribii.goalmate.domain.repository.GoalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository
}