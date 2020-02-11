/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.nordicsemi.android.bluetooth.data.local.database;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import no.nordicsemi.android.bluetooth.data.local.dao.BleDao;
import no.nordicsemi.android.bluetooth.data.local.entity.BleEntity;


/**
 * BleRoomDatabase. Includes code to create the database.
 * After the app creates the database, all further interactions
 * with it happen through the BleEntityViewModel.
 */

@Database(entities = {BleEntity.class}, version = 2, exportSchema = false)
public abstract class BleRoomDatabase extends RoomDatabase {

    public abstract BleDao BleDao();

    private static BleRoomDatabase INSTANCE;

    //singleton
    public static BleRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BleRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BleRoomDatabase.class, "BleEntity_database")
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // This callback is called when the database has opened.
    // In this case, use PopulateDbAsync to populate the database
    // with the initial data set if the database has no entries.
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    // Populate the database with the initial data set
    // only if the database has no entries.
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final BleDao mDao;



        // Initial data set
        private static String[] BleEntitys = new String[100];



        PopulateDbAsync(BleRoomDatabase db) {
            mDao = db.BleDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // If we have no BleEntitys, then create the initial list of BleEntitys.

            return null;
        }
    }
}

