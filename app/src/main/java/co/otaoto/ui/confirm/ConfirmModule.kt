package co.otaoto.ui.confirm

import co.otaoto.di.ActivityScoped
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ConfirmModule {

    @Provides
    @ActivityScoped
    @Named(PARAM_SECRET)
    fun provideSecret(activity: ConfirmActivity): String = activity.intent.getStringExtra(PARAM_SECRET)

    @Provides
    @ActivityScoped
    @Named(PARAM_SLUG)
    fun provideSlug(activity: ConfirmActivity): String = activity.intent.getStringExtra(PARAM_SLUG)

    @Provides
    @ActivityScoped
    @Named(PARAM_KEY)
    fun provideKey(activity: ConfirmActivity): String = activity.intent.getStringExtra(PARAM_KEY)
}
