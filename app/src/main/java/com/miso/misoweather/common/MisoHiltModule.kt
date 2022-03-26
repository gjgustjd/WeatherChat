package com.miso.misoweather.common

import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Qualifier

@Module
@InstallIn(ActivityRetainedComponent::class)
class MisoHiltModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MutableStringLiveData

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MutableNullableStringLiveData

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MutableAnyLiveData

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MutableNullableAnyLiveData

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MutableBooleanLiveData

    @MutableNullableStringLiveData
    @Provides
    fun getNullableStringMutableLiveData(): MutableLiveData<String?> {
        return MutableLiveData()
    }

    @MutableStringLiveData
    @Provides
    fun getStringMutableLiveData(): MutableLiveData<String> {
        return MutableLiveData()
    }

    @MutableBooleanLiveData
    @Provides
    fun getBoolenaMutableLiveData(): MutableLiveData<Boolean> {
        return MutableLiveData()
    }

    @MutableAnyLiveData
    @Provides
    fun getAnyMutableLiveData(): MutableLiveData<Any> {
        return MutableLiveData()
    }

    @MutableNullableAnyLiveData
    @Provides
    fun getNullableAnyMutableLiveData(): MutableLiveData<Any?> {
        return MutableLiveData()
    }

}