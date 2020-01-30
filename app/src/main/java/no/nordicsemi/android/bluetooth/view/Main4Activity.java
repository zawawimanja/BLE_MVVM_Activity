package no.nordicsemi.android.bluetooth.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.bluetooth.R;
import no.nordicsemi.android.bluetooth.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.bluetooth.viewmodels.BlinkyViewModel;

import static no.nordicsemi.android.bluetooth.view.BlinkyActivity.EXTRA_DEVICE;

public class Main4Activity extends AppCompatActivity {

    private BlinkyViewModel mViewModel;

    @BindView(R.id.led_switch)
    Switch mLed;
    @BindView(R.id.button_state)
    TextView mButtonState;
    @BindView(R.id.activity_tool_bar)
    Toolbar mActivityToolbar;
    @BindView(R.id.activity_tool_bar2) Toolbar mActivityToolbar2;
    @BindView(R.id.activity_tool_bar3) Toolbar mActivityToolbar3;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();



        // Configure the view model
        mViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);
        mViewModel.connect(device);

        // Set up views
        final TextView ledState = findViewById(R.id.led_state);
        final LinearLayout progressContainer = findViewById(R.id.progress_container);
        final TextView connectionState = findViewById(R.id.connection_state);
        final View content = findViewById(R.id.device_container);
        final View notSupported = findViewById(R.id.not_supported);

     //   mLed.setOnCheckedChangeListener((buttonView, isChecked) -> mViewModel.toggleLED(isChecked));
        mViewModel.isDeviceReady().observe(this, deviceReady -> {
            progressContainer.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        });
        mViewModel.getConnectionState().observe(this, text -> {
            if (text != null) {


                connectionState.setText(text);
            }
        });
        mViewModel.isConnected().observe(this, this::onConnectionStateChanged);



    }



    private void onConnectionStateChanged(final boolean connected) {
        mLed.setEnabled(connected);
        if (!connected) {
            mLed.setChecked(false);
            mButtonState.setText(R.string.button_unknown);
        }
    }
}
