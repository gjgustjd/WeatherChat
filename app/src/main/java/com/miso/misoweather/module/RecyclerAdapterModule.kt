package com.miso.misoweather.module

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class RecyclerAdapterModule {
    @Provides
    @ActivityScoped
    fun getGridLayoutManager(@ActivityContext context: Context): GridLayoutManager {
        return GridLayoutManager(context, 4)
    }

    @Provides
    @ActivityScoped
    fun getVerticalLinearLayoutManager(@ActivityContext context: Context): LinearLayoutManager {
        return LinearLayoutManager(context)
    }

    @Provides
    @ActivityScoped
    fun getVerticalSpaceItemDecoration(): VerticalSpaceItemDecoration {
        return VerticalSpaceItemDecoration(30)
    }

}