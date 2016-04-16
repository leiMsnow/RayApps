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

import com.ray.aidl.server.IMyTripAidl;
import com.ray.aidl.server.IServerAidl;
import com.ray.aidl.server.MyPackage;

public class MainActivity extends AppCompatActivity {

    private EditText etNum1;
    private EditText etNum2;
    private Button btnAdd;
    private Button btnBoardingpass;
    private TextView tvResult;

    private IServerAidl serverAidl;
    private IMyTripAidl myTripAidl;

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

    private ServiceConnection tripConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myTripAidl = IMyTripAidl.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myTripAidl = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService();
        bindTripService();

        etNum1 = (EditText) findViewById(R.id.et_num1);
        etNum2 = (EditText) findViewById(R.id.et_num2);
        btnAdd = (Button) findViewById(R.id.btn_addition);
        btnBoardingpass = (Button) findViewById(R.id.btn_boardingpass);
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

        btnBoardingpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String info = myTripAidl.getBoardingPass(etNum1.getText().toString()
                            , new MyPackage("太阳眼镜", "沙滩裤", "沙滩鞋", "T恤"));
                    tvResult.setText(info);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void bindTripService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.ray.aidl.server","com.ray.aidl.server.AirService"));
        bindService(intent, tripConn, Context.BIND_AUTO_CREATE);
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
