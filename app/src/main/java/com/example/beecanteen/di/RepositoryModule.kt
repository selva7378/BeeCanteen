package com.example.beecanteen.di

import com.example.beecanteen.data.repository.AdminRepositoryImpl
import com.example.beecanteen.data.repository.AuthRepositoryImpl
import com.example.beecanteen.data.repository.VotingRepositoryImpl
import com.example.beecanteen.domain.repository.admin.AdminRepository
import com.example.beecanteen.domain.repository.authentication.AuthRepository
import com.example.beecanteen.domain.repository.voting.VotingRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideAdminRepository(
        firestore: FirebaseFirestore
    ): AdminRepository {

        return AdminRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideVotingRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): VotingRepository {
        return VotingRepositoryImpl(auth = auth, firestore = firestore)
    }
}