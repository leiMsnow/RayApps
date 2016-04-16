package com.ray.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ray.aidl.server.IServerAidl;

public class MainActivity extends AppCompatActivity {

    private EditText etNum1;
    private EditText etNum2;
    private Button btnAdd;
    private TextView tvResult;

    private IServerAidl serverAidl;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            serverAidl = IServerAidl.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverAidl = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService();

        etNum1 = (EditText) findViewById(R.id.et_num1);
        etNum2 = (EditText) findViewById(R.id.et_num2);
        btnAdd = (Button) findViewById(R.id.btn_addition);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    int res = serverAidl.add(Integer.parseInt(etNum1.getText().toString()),
                            Integer.parseInt(etNum2.getText().toString()));
                    tvResult.setText(String.valueOf(res));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.ray.aidl.server", "com.ray.aidl.server.RemoteService"));
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
