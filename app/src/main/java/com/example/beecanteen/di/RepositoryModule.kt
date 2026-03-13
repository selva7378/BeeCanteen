package com.example.beecanteen.di

import com.example.beecanteen.data.repository.AdminRepositoryImpl
import com.example.beecanteen.domain.repository.admin.AdminRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideVotingRepository(
        firestore: FirebaseFirestore
    ): AdminRepository {

        return AdminRepositoryImpl(firestore)
    }
}