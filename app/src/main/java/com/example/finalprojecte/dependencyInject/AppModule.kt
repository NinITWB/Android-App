package com.example.finalprojecte.dependencyInject

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.finalprojecte.firebase.FireBaseFunction
import com.example.finalprojecte.utility.Constanst.INTRODUCTION_SP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
//The dependencies will exist as long as the app is alive
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides           //provide dependencies
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    fun provideIntroductionSP(
        application : Application
    ) = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun fireBaseFunction(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FireBaseFunction(firestore, firebaseAuth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}