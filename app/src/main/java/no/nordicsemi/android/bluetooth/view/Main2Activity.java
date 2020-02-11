package no.nordicsemi.android.bluetooth.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import no.nordicsemi.android.bluetooth.R;
import no.nordicsemi.android.bluetooth.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.bluetooth.data.local.entity.BleEntity;
import no.nordicsemi.android.bluetooth.factory.ViewModelFactory;

import no.nordicsemi.android.bluetooth.viewmodels.BlinkyViewModel;

import static no.nordicsemi.android.bluetooth.view.BlinkyActivity.EXTRA_DEVICE;


public class Main2Activity extends AppCompatActivity {


    public static String TAG = "Main2Activity";
    private BlinkyViewModel mViewModel;

    TextView display;
    TextView display1;
    TextView text;
    TextView send;
    Button btn,delete;
    DiscoveredBluetoothDevice device;
    private EditText mEditWordView,mEditWordView1;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_WORD_ACTIVITY_REQUEST_CODE = 2;
    public static final int SAME_WORD_ACTIVITY_REQUEST_CODE = 3;
    public static final String EXTRA_DATA_UPDATE_WORD = "extra_word_to_be_updated";
    public static final String EXTRA_DATA_ID = "extra_data_id";

    /*
     * Step 1: Here as mentioned in Step 5, we need to
     * inject the ViewModelFactory. The ViewModelFactory class
     * has a list of ViewModels and will provide
     * the corresponding ViewModel in this activity
     * */
    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        /*
         * Step 2: Remember in our ActivityModule, we
         * defined MainActivity injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity
         * */
        AndroidInjection.inject(this);
        final Intent intent = getIntent();
        device = intent.getParcelableExtra(EXTRA_DEVICE);
        Log.i(TAG, "Device"+device);



        text=findViewById(R.id.text);
        btn=findViewById(R.id.btn);
        send=findViewById(R.id.send);
        display = findViewById(R.id.display);

        delete = findViewById(R.id.delete);
        // Configure the view model
        //    mViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);
        mViewModel = ViewModelProviders.of(this,viewModelFactory).get(BlinkyViewModel.class);
        mViewModel.connect(device);



        mViewModel.getAllWords().observe(this, new Observer<List<BleEntity>>() {
            @Override
            public void onChanged(List<BleEntity> bleEntities) {
                //  adapter.setWords(words);
                Log.i(TAG,bleEntities+"Bleentity");




                setTextViewFromList(bleEntities,display);
           //     setTextViewFromReceive(bleEntities,display1);
            }


        });

        String sender="YBPPP";

        //send
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.sendData(sender);
            }
        });


        mViewModel.isConnected().observe(this, this::onConnectionStateChanged);


        mViewModel.getTXState().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                send.setText(s);
            }
        });




        //receive
        mViewModel.getReceive2().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {



                if(s.contains("KKK")){

                    mEditWordView.setText(" ");
                    mEditWordView1.setText(s);
                }
                else if(s.contains("JJJ")){

                    mEditWordView.setText(" ");
                    mEditWordView1.setText(" ");
                }
                else {
                    mEditWordView.setText(s);
                }


                Log.i(TAG,"Received MessageMain2"+s);
            }
        });

        mEditWordView = findViewById(R.id.edit_word);
        mEditWordView1 = findViewById(R.id.edit_word1);




        Button button = findViewById(R.id.button_save);

        // When the user presses the Save button, create a new Intent for the reply.
        // The reply Intent will be sent back to the calling activity (in this case, MainActivity).
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Create a new Intent for the reply.
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditWordView1.getText())) {
                    // No word was entered, set the result accordingly.
                    setResult(RESULT_CANCELED, replyIntent);
                } else {

//
//                    // Get the new word that the user entered.


                    BleEntity word = new BleEntity(mEditWordView1.getText().toString());
                    // Save the data.

                    // mViewModel.toggleLED(word1);
                    mViewModel.update(word);
                    mViewModel.insert(word);
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mViewModel.deleteAll();
            }
        });



    }


    public void setTextViewFromReceive(List<BleEntity> arraylist, TextView textview) {
        //Variable to hold all the values
        String output = "";

        for (int i = 0; i < arraylist.size(); i++) {
            //Append all the values to a string
            output += arraylist.get(i).getReceive();//whatever you want to show here like shelving or cordinalpoint use getCordinalPoint()
            output += "\n";
        }

        //Set the textview to the output string
        textview.setText(output);
    }


    public void setTextViewFromList(List<BleEntity> arraylist, TextView textview) {
        //Variable to hold all the values
        String output = "";

        for (int i = 0; i < arraylist.size(); i++) {
            //Append all the values to a string
            output += arraylist.get(i).getWord();//whatever you want to show here like shelving or cordinalpoint use getCordinalPoint()
            output += "\n";
        }

        //Set the textview to the output string
        textview.setText(output);
    }


    private void onConnectionStateChanged(final boolean connected) {

        if (!connected) {

            text.setText("Not Connected");
        }else{
            text.setText("Connected");
        }

        Log.i(TAG,"Main2Activity"+connected);


    }


}
