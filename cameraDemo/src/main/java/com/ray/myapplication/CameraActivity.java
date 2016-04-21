package com.ray.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CameraActivity extends AppCompatActivity {

    @Bind(R.id.sv_camera)
    SurfaceView svCamera;
    @Bind(R.id.btn_capture)
    Button btnCapture;
    @Bind(R.id.iv_result)
    ImageView ivResult;

    private SurfaceHolder mHolder;
    private Camera mCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        mHolder = svCamera.getHolder();

        mHolder.addCallback(new SurfaceHolderCallback());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            startCamera();
        }
        startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    public void onClick(View view) {
        if (view == svCamera) {
            if (mCamera != null)
                mCamera.autoFocus(null);
        } else if (view == btnCapture) {
            takePicture();
        }
    }

    private void takePicture() {

        if (mCamera == null)
            return;

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPictureSize(1280, 720);
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    mCamera.takePicture(null, null, mPictureCallback);
                }
            }
        });

    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mCamera.stopPreview();
            startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseCamera();
        }
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            parseData(data);
        }
    };

    private void parseData(byte[] data) {

        File tempFile = new File(Environment.getExternalStorageDirectory().getPath() + "/temp.png");
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {

            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(data);
            fos.close();
            sendPicResult(tempFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPicResult(String picPath) {

        try {

            FileInputStream fis = new FileInputStream(picPath);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            ivResult.setVisibility(View.VISIBLE);
            ivResult.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void startCamera() {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            mCamera = null;
        }
    }

    private void startPreview() {

        if (mCamera == null) return;
        if (mHolder == null) return;

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            mCamera.autoFocus(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void releaseCamera() {

        if (mCamera == null)
            return;

        try {
            mCamera.setPreviewDisplay(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }
}
