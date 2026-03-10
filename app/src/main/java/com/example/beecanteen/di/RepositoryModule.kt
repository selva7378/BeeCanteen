package com.example.beecanteen.di

import com.example.beecanteen.data.repository.BeeCanteenRepositoryImpl
import com.example.beecanteen.domain.repository.BeeCanteenRepository
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
    ): BeeCanteenRepository {

        return BeeCanteenRepositoryImpl(firestore)
    }
}