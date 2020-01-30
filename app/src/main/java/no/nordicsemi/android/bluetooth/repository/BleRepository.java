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

package no.nordicsemi.android.bluetooth.repository;

import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.WriteRequest;
import no.nordicsemi.android.bluetooth.data.local.dao.BleDao;
import no.nordicsemi.android.bluetooth.data.local.database.BleRoomDatabase;
import no.nordicsemi.android.bluetooth.data.local.entity.BleEntity;

import no.nordicsemi.android.bluetooth.profile.BlinkyManagerCallbacks;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;


/**
 * This class holds the implementation code for the methods that interact with the database.
 * Using a repository allows us to group the implementation methods together,
 * and allows the BleEntityViewModel to be a clean interface between the rest of the app
 * and the database.
 *
 * For insert, update and delete, and longer-running queries,
 * you must run the database interaction methods in the background.
 *
 * Typically, all you need to do to implement a database method
 * is to call it on the data access object (DAO), in the background if applicable.
 */

public class BleRepository extends BleManager<BlinkyManagerCallbacks> {

    private BleDao mBleDao;

    private LiveData<List<BleEntity>> mAllBleEntitys;
    /** Nordic Blinky BlinkService UUID. */
    public final static UUID LBS_UUID_SERVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    /** BUTTON characteristic UUID. */
    private final static UUID LBS_UUID_BUTTON_CHAR = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    /** LED characteristic UUID. */
    private final static UUID LBS_UUID_LED_CHAR = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    String text;
    private LogSession mLogSession;
    private boolean mSupported;
    private static BleRepository managerInstance = null;
    private BluetoothGattCharacteristic mRXCharacteristic, mTXCharacteristic;
    private boolean mUseLongWrite = true;
    public static final String TAG="BleRepository";


    @Singleton
    public BleRepository(Application application) {
        super(application);
        BleRoomDatabase db = BleRoomDatabase.getDatabase(application);
        mBleDao = db.BleDao();
        mAllBleEntitys = mBleDao.getAllBleEntitys();
    }


    /** singleton object */
    public static synchronized BleRepository getBlinkyManager(final Application application) {
        if (managerInstance == null) {
            managerInstance = new BleRepository(application);
        }
        return managerInstance;
    }




    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    /**
     * Sets the log session to be used for low level logging.
     * @param session the session, or null, if nRF Logger is not installed.
     */
    public void setLogger(@Nullable final LogSession session) {
        this.mLogSession = session;
    }

    @Override
    public void log(final int priority, @NonNull final String message) {
        // The priority is a Log.X constant, while the Logger accepts it's log levels.
        Logger.log(mLogSession, LogContract.Log.Level.fromPriority(priority), message);
    }

    @Override
    protected boolean shouldClearCacheWhenDisconnected() {
        return !mSupported;
    }


    /**
     * BluetoothGatt callbacks for connection/disconnection, service discovery,
     * receiving indication, etc.
     */

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {
        @Override
        protected void initialize() {

            setNotificationCallback(mTXCharacteristic).with((device, data) -> { text = data.getStringValue(0);
                log(LogContract.Log.Level.APPLICATION, "\"" + text + "\" receivedfromMCU");
                Log.i(TAG, "Datareceived: " + text);

                //pass to callback
                //transfer data received to activity
            //  mCallbacks.onRXChanged(device, text);

                if(text.contains("XAJJJ")){

                    mCallbacks.onRXChanged(device," JJJ");
                }
                else if(text.contains("XBSUCCESS1")){

                    mCallbacks.onRXChanged(device, "SUCCESS1");
                }
                else if(text.contains("YAKKK")){

                    mCallbacks.onRXChanged(device," KKK");
                }
                else if(text.contains("YBSUCCESS2")){

                    mCallbacks.onRXChanged(device,"SUCCESS2");
                }




            });

            requestMtu(260).enqueue();
            enableNotifications(mTXCharacteristic).enqueue();

        }

        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(LBS_UUID_SERVICE);
            if (service != null) {
                mRXCharacteristic = service.getCharacteristic(LBS_UUID_BUTTON_CHAR);
                mTXCharacteristic = service.getCharacteristic(LBS_UUID_LED_CHAR);
            }
            boolean writeRequest = false;
            boolean writeCommand = false;
            if (mRXCharacteristic != null) {
                final int rxProperties = mRXCharacteristic.getProperties();
                writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                writeCommand = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0;

                // Set the WRITE REQUEST type when the characteristic supports it.
                // This will allow to send long write (also if the characteristic support it).
                // In case there is no WRITE REQUEST property, this manager will divide texts
                // longer then MTU-3 bytes into up to MTU-3 bytes chunks.
                if (writeRequest)
                    mRXCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                else
                    mUseLongWrite = false;
            }

            return mRXCharacteristic != null && mTXCharacteristic != null && (writeRequest || writeCommand);
        }

        @Override
        protected void onDeviceDisconnected() {
            mRXCharacteristic = null;
            mTXCharacteristic = null;
            mUseLongWrite = true;
        }
    };




    /**
     * Sends the given text to TX characteristic.
     * @param text the text to be sent
     */
    public void send(final String text) {

        // Are we connected?
        if (mRXCharacteristic == null)
            return;

        if (!TextUtils.isEmpty(text)) {
            final WriteRequest request = writeCharacteristic(mRXCharacteristic,
                    text.getBytes()).with((device, data) -> log(LogContract.Log.Level.APPLICATION, "\""
                    + data.getStringValue(0) + "\" senttoMCU"));

            //pass to callback
            mCallbacks.onTXChanged(text);



            if (!mUseLongWrite) {
                // This will automatically split the long data into MTU-3-byte long packets.
                request.split();
            }

            request.enqueue();
        }
    }
    

    public LiveData<List<BleEntity>> getAllBleEntitys() {
        return mAllBleEntitys;
    }

    public void insert(BleEntity BleEntity) {
        new insertAsyncTask(mBleDao).execute(BleEntity);
    }

    public void update(BleEntity BleEntity)  {
        new updateBleEntityAsyncTask(mBleDao).execute(BleEntity);
    }

    public void deleteAll()  {
        new deleteAllBleEntitysAsyncTask(mBleDao).execute();
    }

    // Must run off main thread
    public void deleteBleEntity(BleEntity BleEntity) {
        new deleteBleEntityAsyncTask(mBleDao).execute(BleEntity);
    }

    // Static inner classes below here to run database interactions in the background.

    /**
     * Inserts a BleEntity into the database.
     */
    private static class insertAsyncTask extends AsyncTask<BleEntity, Void, Void> {

        private BleDao mAsyncTaskDao;

        insertAsyncTask(BleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BleEntity... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    /**
     * Deletes all BleEntitys from the database (does not delete the table).
     */
    private static class deleteAllBleEntitysAsyncTask extends AsyncTask<Void, Void, Void> {
        private BleDao mAsyncTaskDao;

        deleteAllBleEntitysAsyncTask(BleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    /**
     *  Deletes a single BleEntity from the database.
     */
    private static class deleteBleEntityAsyncTask extends AsyncTask<BleEntity, Void, Void> {
        private BleDao mAsyncTaskDao;

        deleteBleEntityAsyncTask(BleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BleEntity... params) {
            mAsyncTaskDao.deleteBleEntity(params[0]);
            return null;
        }
    }

    /**
     *  Updates a BleEntity in the database.
     */
    private static class updateBleEntityAsyncTask extends AsyncTask<BleEntity, Void, Void> {
        private BleDao mAsyncTaskDao;

        updateBleEntityAsyncTask(BleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BleEntity... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
