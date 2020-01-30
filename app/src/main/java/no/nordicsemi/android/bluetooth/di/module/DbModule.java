package no.nordicsemi.android.bluetooth.di.module;


import android.app.Application;


import androidx.annotation.NonNull;
import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import no.nordicsemi.android.bluetooth.data.local.dao.BleDao;
import no.nordicsemi.android.bluetooth.data.local.database.BleRoomDatabase;

@Module
public class DbModule {

    /*
     * The method returns the Database object
     * */
    @Provides
    @Singleton
    BleRoomDatabase provideDatabase(@NonNull Application application) {
        return Room.databaseBuilder(application,
                BleRoomDatabase.class, "Entertainment.db")
                .allowMainThreadQueries().build();
    }


    /*
     * We need the MovieDao module.
     * For this, We need the AppDatabase object
     * So we will define the providers for this here in this module.
     * */

    @Provides
    @Singleton
    BleDao provideMovieDao(@NonNull BleRoomDatabase appDatabase) {
        return appDatabase.BleDao();
    }
}