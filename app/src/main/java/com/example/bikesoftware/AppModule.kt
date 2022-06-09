package com.example.bikesoftware

import android.app.Application
import com.example.data.repository.TripRepositoryImpl
import com.example.domain.repository.TripRepository
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSqlDriver(app: Application): SqlDriver {
        return AndroidSqliteDriver(
            schema = TripDatabase.Schema,
            context = app,
            name = "trips.db"
        )
    }

    @Provides
    @Singleton
    fun provideTripRepository(driver: SqlDriver): TripRepository = TripRepositoryImpl(TripDatabase(driver))
}
