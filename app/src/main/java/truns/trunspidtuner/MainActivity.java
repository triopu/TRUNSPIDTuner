package truns.trunspidtuner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements View.OnClickListener{
    EditText inputKP, inputKI, inputKD;
    TextInputLayout inputLayoutKP, inputLayoutKI, inputLayoutKD;
    ToggleButton btnSend, connectBT;
    Boolean bltConnect = false;
    private Handler theHandler;

    Boolean sendPID = false;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case bluetoothActivity.SUCCESS_CONNECT:
                    bluetoothActivity.connectedThread = new bluetoothActivity.ConnectedThread((BluetoothSocket)msg.obj);
                    Toast.makeText(getApplicationContext(),"Connected!",Toast.LENGTH_SHORT).show();
                    bltConnect = true;
                    String s = "Successfully  Connected";
                    bluetoothActivity.connectedThread.start();
                    break;
                case bluetoothActivity.MESSAGE_READ:
                    byte[] readBuf = (byte[])msg.obj;
                    int i = 0;
                    for (i = 0; i < readBuf.length && readBuf[i] != 0; i++) {
                    }
                    final String Income = new String(readBuf,0,i);
                    String[] items = Income.split("\\*");
                    for(String item : items){
                    }
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputLayoutKP = (TextInputLayout) findViewById(R.id.inKP);
        inputLayoutKI = (TextInputLayout) findViewById(R.id.inKI);
        inputLayoutKD = (TextInputLayout) findViewById(R.id.inKD);
        inputKP = (EditText) findViewById(R.id.input_kp);
        inputKI = (EditText) findViewById(R.id.input_ki);
        inputKD = (EditText) findViewById(R.id.input_kd);

        btnSend = (ToggleButton) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        connectBT = (ToggleButton) findViewById(R.id.connectBT);
        connectBT.setOnClickListener(this);

        inputKP.addTextChangedListener(new MyTextWatcher(inputKP));
        inputKI.addTextChangedListener(new MyTextWatcher(inputKI));
        inputKD.addTextChangedListener(new MyTextWatcher(inputKD));

        bluetoothActivity.gethandler(mHandler);
        theHandler  = new Handler();
        theHandler.post(theUpdate);
    }

    private Runnable theUpdate = new Runnable() {
        @Override
        public void run() {
            sendKPKIKD();
            theHandler.postDelayed(this, 10);
        }
    };

    private void submitForm() {
        if (!validateKP()) {
            btnSend.setChecked(false);
            return;
        }

        if (!validateKI()) {
            btnSend.setChecked(false);
            return;
        }

        if (!validateKD()) {
            btnSend.setChecked(false);
            return;
        }

        if (!bltConnect) {
            btnSend.setChecked(false);
            return;
        }

        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
        sendPID = true;
        Toast.makeText(this, "Sending", Toast.LENGTH_SHORT).show();
    }

    private boolean validateKP() {
        if (inputKP.getText().toString().trim().isEmpty()) {
            inputLayoutKP.setError(getString(R.string.ErrKP));
            requestFocus(inputKP);
            return false;
        } else {
            inputLayoutKP.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateKI() {
        String email = inputKI.getText().toString().trim();

        if (email.isEmpty()) {
            inputLayoutKI.setError(getString(R.string.ErrKI));
            requestFocus(inputKI);
            return false;
        } else {
            inputLayoutKI.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateKD() {
        if (inputKD.getText().toString().trim().isEmpty()) {
            inputLayoutKD.setError(getString(R.string.ErrKD));
            requestFocus(inputKD);
            return false;
        } else {
            inputLayoutKD.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                if (btnSend.isChecked()){
                    submitForm();
                }else{
                    sendPID = false;
                    Toast.makeText(this, "No Send", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.connectBT:
                if (connectBT.isChecked()){
                    startActivity(new Intent("android.intent.action.BT1"));
                    if(!bltConnect){
                        connectBT.setChecked(false);
                    }
                }else {
                    bluetoothActivity.disconnect();
                    bltConnect = false;
                    Toast.makeText(this, "Bluetooth Disconnected", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void sendKPKIKD(){
        if (sendPID) {
            Log.d("Sending: ","Yes");
            Double KP = Double.parseDouble(inputKP.getText().toString());
            Double KI = Double.parseDouble(inputKI.getText().toString());
            Double KD = Double.parseDouble(inputKD.getText().toString());
            StringBuilder kP = new StringBuilder();
            StringBuilder kI = new StringBuilder();
            StringBuilder kD = new StringBuilder();

            kP.append('p').append(KP).append(':');
            kI.append('i').append(KI).append(':');
            kD.append('d').append(KD).append(':');

            String lastP = kP.toString();
            String lastI = kI.toString();
            String lastD = kD.toString();

            Log.d("SENDPID: ", lastP + " : " + lastI + " : " + lastD);
            if (bluetoothActivity.connectedThread != null) {
                bluetoothActivity.connectedThread.write(lastP);
                bluetoothActivity.connectedThread.write(lastI);
                bluetoothActivity.connectedThread.write(lastD);
            }
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_kp:
                    validateKP();
                    break;
                case R.id.input_ki:
                    validateKI();
                    break;
                case R.id.input_kd:
                    validateKD();
                    break;
            }
        }
    }
}