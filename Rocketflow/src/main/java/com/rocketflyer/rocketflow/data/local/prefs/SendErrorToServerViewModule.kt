package com.rocketflyer.rocketflow.data.local.prefs

import androidx.work.ListenableWorker
import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.MapKey
import dagger.Module
import dagger.Provides
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)
@Module
class SendErrorToServerViewModule {
    @Provides
    fun providePostFileErrorToServerViewModel(
        dataManager: DataManager,
        schedulerProvider: SchedulerProvider
    ): PostFileErrorToServerViewModel {
        return PostFileErrorToServerViewModel(dataManager, schedulerProvider)
    }

}