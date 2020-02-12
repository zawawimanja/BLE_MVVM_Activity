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

package no.nordicsemi.android.bluetooth.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import no.nordicsemi.android.bluetooth.BlinkyApplication;
import no.nordicsemi.android.bluetooth.R;
import no.nordicsemi.android.bluetooth.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.bluetooth.factory.ViewModelFactory;

import no.nordicsemi.android.bluetooth.viewmodels.BlinkyViewModel;


@SuppressWarnings("ConstantConditions")
public class BlinkyActivity extends AppCompatActivity {

	public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";
	private BlinkyViewModel mViewModel;


	@BindView(R.id.button_state) TextView mButtonState;
	@BindView(R.id.activity_tool_bar) Toolbar mActivityToolbar;
	@BindView(R.id.activity_tool_bar2) Toolbar mActivityToolbar2;
	@BindView(R.id.activity_tool_bar3) Toolbar mActivityToolbar3;
	@BindView(R.id.activity_tool_bar4) Toolbar mActivityToolbar4;

   Context context;


	public static final String  TAG ="BlinkyActivity";
	DiscoveredBluetoothDevice device;
	Button btn;


	/*
	 * Step 1: Here as mentioned in Step 5, we need to
	 * inject the ViewModelFactory. The ViewModelFactory class
	 * has a list of ViewModels and will provide
	 * the corresponding ViewModel in this activity
	 * */
	@Inject
	ViewModelFactory viewModelFactory;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		/*
		 * Step 2: Remember in our ActivityModule, we
		 * defined MainActivity injection? So we need
		 * to call this method in order to inject the
		 * ViewModelFactory into our Activity
		 * */
		AndroidInjection.inject(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blinky);
		ButterKnife.bind(this);


		SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
		btn=findViewById(R.id.btn);

		final Intent intent = getIntent();
		device = intent.getParcelableExtra(EXTRA_DEVICE);
		Log.i(TAG, "Device"+device);

		final String deviceName = device.getName();
		final String deviceAddress = device.getAddress();

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(deviceName);
		getSupportActionBar().setSubtitle(deviceAddress);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);

		// Configure the view model
		//mViewModel = ViewModelProviders.of(this,viewModelFactory).get(BlinkyViewModel.class);
		mViewModel.connect(device);

		// Set up views

		final LinearLayout progressContainer = findViewById(R.id.progress_container);
		final TextView connectionState = findViewById(R.id.connection_state);
		final View content = findViewById(R.id.device_container);
		final View notSupported = findViewById(R.id.not_supported);




		mViewModel.isDeviceReady().observe(this, deviceReady -> {
			progressContainer.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		});

		mViewModel.getConnectionState().observe(this, text -> {
			if (text != null) {
				progressContainer.setVisibility(View.VISIBLE);
				notSupported.setVisibility(View.GONE);
				connectionState.setText(text);
				Log.i("State","State"+text);
			}


		});

		mViewModel.isConnected().observe(this, this::onConnectionStateChanged);

		mViewModel.isSupported().observe(this, supported -> {
			if (!supported) {
				progressContainer.setVisibility(View.GONE);
				notSupported.setVisibility(View.VISIBLE);
			}
		});

		mActivityToolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewModel.sendData("XAO");
				final Intent controlBlinkIntent = new Intent(getApplicationContext(), MainActivity.class);
				controlBlinkIntent.putExtra(EXTRA_DEVICE, device);
				startActivity(controlBlinkIntent);
			}
		});


		mActivityToolbar2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewModel.sendData("YAO");
				final Intent controlBlinkIntent = new Intent(getApplicationContext(), Main2Activity.class);
				controlBlinkIntent.putExtra(EXTRA_DEVICE, device);
				startActivity(controlBlinkIntent);
			}
		});


	}

	@OnClick(R.id.action_clear_cache)
	public void onTryAgainClicked() {
		mViewModel.reconnect();
	}

	private void onConnectionStateChanged(final boolean connected) {

		if (!connected) {

			mButtonState.setText("Not Connected");
		}else{
			mButtonState.setText("Connected");
		}
		Log.i(TAG,"BlinkyActivity"+connected);
	}







}
