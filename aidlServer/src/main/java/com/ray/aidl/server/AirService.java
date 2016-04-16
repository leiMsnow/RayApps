package com.ray.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by zhangleilei on 4/16/16.
 */
public class AirService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    private IBinder iBinder = new IMyTripAidl.Stub() {
        @Override
        public String getBoardingPass(String id, MyPackage myPackage) throws RemoteException {
            if (TextUtils.isEmpty(id)) {
                return "请出示身份证";
            }
            if (id.equals("110")) {
                return "请收好您的登机牌," + myPackage.toString();
            }
            return "对不起，您没有在我航办理飞行业务";
        }
    };


}
