/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.bluetooth.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import no.nordicsemi.android.bluetooth.R;
import no.nordicsemi.android.bluetooth.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.bluetooth.data.local.database.BleRoomDatabase;
import no.nordicsemi.android.bluetooth.data.local.entity.BleEntity;

import no.nordicsemi.android.bluetooth.profile.BlinkyManagerCallbacks;
import no.nordicsemi.android.bluetooth.repository.BleRepository;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

/**The ViewModel will initialise an instance of the Repository class and update the UI based with this data.
 *
 *
 */

public class BlinkyViewModel extends AndroidViewModel implements BlinkyManagerCallbacks {




	private LiveData<List<BleEntity>> mAllWords;

	private final BleRepository mBlinkyManager;
	private BluetoothDevice mDevice;

	public static String TAG = "BlinkyViewModel";

	// Connection states Connecting, Connected, Disconnecting, Disconnected etc.
	private final MutableLiveData<Boolean> mBondingState= new MutableLiveData<>();

	// Connection states Connecting, Connected, Disconnecting, Disconnected etc.
	private final MutableLiveData<String> mConnectionState = new MutableLiveData<>();

	// Flag to determine if the device is connected
	private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();

	// Flag to determine if the device has required services
	private final MutableLiveData<Boolean> mIsSupported = new MutableLiveData<>();

	// Flag to determine if the device is ready
	private final MutableLiveData<Void> mOnDeviceReady = new MutableLiveData<>();

	// Flag to determine  prepare to receive message
	private final MutableLiveData<String> mTXState = new MutableLiveData<>();
	// Flag to determine  prepare to send message
	private final MutableLiveData<String> mRXState = new MutableLiveData<>();

	// Flag to determine  prepare to send message
	private final MutableLiveData<String> mReceive2 = new MutableLiveData<>();


	public LiveData<Void> isDeviceReady() {
		return mOnDeviceReady;
	}

	public LiveData<String> getConnectionState() {
		return mConnectionState;
	}

	public LiveData<Boolean> isConnected() {
		return mIsConnected;
	}

	public LiveData<String> getRXState() {
		return mRXState;
	}

	public LiveData<String> getReceive2() {
		return mReceive2;
	}

	public LiveData<String> getTXState() {
		return mTXState;
	}

	public LiveData<Boolean> isSupported() {
		return mIsSupported;
	}


	@Inject
	public BlinkyViewModel(@NonNull final Application application) {
		super(application);

		// Initialize the manager as singleton
		//connection will lost
		//mBlinkyManager = BleRepository.getInstance(getApplication());
	   mBlinkyManager = new BleRepository(getApplication());


		mBlinkyManager.setGattCallbacks(this);
		mAllWords = 	mBlinkyManager.getAllBleEntitys();

		//mAllWords = mRepository.getAllBleEntitys();
	}

	/**
	 * Connect to peripheral.
	 */
	public void connect(@NonNull final DiscoveredBluetoothDevice device) {
		// Prevent from calling again when called again (screen orientation changed)
		if (mDevice == null) {
			mDevice = device.getDevice();
			final LogSession logSession
					= Logger.newSession(getApplication(), null, device.getAddress(), device.getName());
			mBlinkyManager.setLogger(logSession);
			reconnect();
		}
	}

	/**
	 * Reconnects to previously connected device.
	 * If this device was not supported, its services were cleared on disconnection, so
	 * reconnection may help.
	 */
	public void reconnect() {
		if (mDevice != null) {
			mBlinkyManager.connect(mDevice)
					.retry(3, 100)
					.useAutoConnect(false)
					.enqueue();
		}
	}

	/**
	 * Disconnect from peripheral.
	 */
	private void disconnect() {
		mDevice = null;
		mBlinkyManager.disconnect().enqueue();
	}


	@Override
	protected void onCleared() {
		super.onCleared();
		if (mBlinkyManager.isConnected()) {
			disconnect();
		}
	}




	@Override
	public void onDeviceConnecting(@NonNull final BluetoothDevice device) {
		mConnectionState.postValue(getApplication().getString(R.string.state_connecting));
	}

	@Override
	public void onDeviceConnected(@NonNull final BluetoothDevice device) {
		mIsConnected.postValue(true);
		mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services));
	}

	@Override
	public void onDeviceDisconnecting(@NonNull final BluetoothDevice device) {
		mIsConnected.postValue(false);
	}

	@Override
	public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
		mIsConnected.postValue(false);
	}

	@Override
	public void onLinkLossOccurred(@NonNull final BluetoothDevice device) {
		mIsConnected.postValue(false);
	}

	@Override
	public void onServicesDiscovered(@NonNull final BluetoothDevice device,
									 final boolean optionalServicesFound) {
		mConnectionState.postValue(getApplication().getString(R.string.state_initializing));
	}

	@Override
	public void onDeviceReady(@NonNull final BluetoothDevice device) {
		mIsSupported.postValue(true);
		mConnectionState.postValue(null);
		mOnDeviceReady.postValue(null);
	}

	@Override
	public void onBondingRequired(@NonNull BluetoothDevice device) {

	}

	@Override
	public void onBonded(@NonNull BluetoothDevice device) {

	}

	@Override
	public void onBondingFailed(@NonNull BluetoothDevice device) {

	}

	@Override
	public void onError(@NonNull final BluetoothDevice device,
						@NonNull final String message, final int errorCode) {
		// TODO implement
	}

	@Override
	public void onDeviceNotSupported(@NonNull final BluetoothDevice device) {
		mConnectionState.postValue(null);
		mIsSupported.postValue(false);
	}


	//receive
	@Override
	public void onRXChanged(@NonNull final BluetoothDevice device, final String dataReceived) {


		mRXState.postValue(dataReceived);


	}

	//receive
	@Override
	public void onReceive2(@NonNull final BluetoothDevice device, final String dataReceived) {


		mReceive2.postValue(dataReceived);

	}

	@Override
	public void onTXChanged( String on) {
		mTXState.postValue(on);
	}

	public LiveData<List<BleEntity>> getAllWords() {
		return mAllWords;
	}

	public void insert(BleEntity word) {
		mBlinkyManager.insert(word);
	}

    public void sendData(final String  isOn) {
		mBlinkyManager.send(isOn);

	}


    public void deleteAll() {
		mBlinkyManager.deleteAll();
	}
//
//	public void deleteWord(BleEntity word) {
//		mRepository.deleteBleEntity(word);
//	}
//
	public void update(BleEntity word) {
		mBlinkyManager.update(word);
	}

}
