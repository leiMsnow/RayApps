// IMyTripAidl.aidl
package com.ray.aidl.server;

// Declare any non-default types here with import statements

import com.ray.aidl.server.MyPackage;

interface IMyTripAidl {

   String getBoardingPass(String id,in MyPackage myPackage);

}
