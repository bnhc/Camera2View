/*
 *
 * SGLViewActivity.java
 *
 * Created by Wuwang on 2016/10/15
 */
package com.dbb.camera2view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;


/**
 * Description:
 */
public class SGLViewActivity extends Activity implements View.OnClickListener {

    private static final String TAG = SGLViewActivity.class.getName();
    private Camera2GLSurfaceView mGLView;


    private TextView scale_tv;
    private TextView left_tv;
    private TextView right_tv;
    private TextView up_tv;
    private TextView down_tv;

    private final float value = 1.0f;

    private final float default_left = -value;
    private final float default_right = value;
    private final float default_up = value;
    private final float default_down = -value;

    private float left = default_left, right = default_right, up = default_up, down = default_down;
    private float centerX = 0f, centerY = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        initView();
        //Camera2Proxy mCameraProxy = mGLView.getCameraProxy();
    }

    private void initView() {
        mGLView = (Camera2GLSurfaceView) findViewById(R.id.camera_view);
        Button scale_large_bt = (Button) findViewById(R.id.scale_large_bt);
        scale_large_bt.setOnClickListener(this);
        Button scale_small_bt = (Button) findViewById(R.id.scale_small_bt);
        scale_small_bt.setOnClickListener(this);
        Button translate_left_bt = (Button) findViewById(R.id.translate_left_bt);
        translate_left_bt.setOnClickListener(this);

        Button translate_down_bt = (Button) findViewById(R.id.translate_down_bt);
        translate_down_bt.setOnClickListener(this);

        Button translate_right_bt = (Button) findViewById(R.id.translate_right_bt);
        translate_right_bt.setOnClickListener(this);

        Button translate_up_bt = (Button) findViewById(R.id.translate_up_bt);
        translate_up_bt.setOnClickListener(this);

        scale_tv = (TextView) findViewById(R.id.scale_tv);
        left_tv = (TextView) findViewById(R.id.left_tv);
        right_tv = (TextView) findViewById(R.id.right_tv);
        up_tv = (TextView) findViewById(R.id.up_tv);
        down_tv = (TextView) findViewById(R.id.down_tv);
        updateView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    private float targetScale = 1.0f;

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scale_large_bt:
                targetScale = targetScale + 0.1f;
                scaleCaclu(targetScale);
                updateView();
                if (targetScale > 4.0f) targetScale = 4.0f;
                mGLView.scale(targetScale);
                mGLView.requestRender();
                break;
            case R.id.scale_small_bt:
                targetScale = targetScale - 0.1f;
                if (targetScale < 1.0f) targetScale = 1.0f;
                scaleCaclu(targetScale);
                mGLView.scale(targetScale);
                if (left > -value) {
                    float left_offset = -value - left;
                    xTra(left_offset);
                    mGLView.translateM(left_offset, 0);
                }
                if (right < value) {
                    float right_offset = value - right;
                    xTra(right_offset);
                    mGLView.translateM(right_offset, 0);
                }
                if (up <value) {
                    float up_offset = value - up;
                    yTra(up_offset);
                    mGLView.translateM(0, up_offset);
                }
                if (down > -value) {
                    float down_offset = -value - down;
                    yTra(down_offset);
                    mGLView.translateM(0, down_offset);
                }
                mGLView.requestRender();
                updateView();
                break;
            case R.id.translate_down_bt:
                float tmp_up = 0.1f;
                if (up < (value+0.1)) {
                    tmp_up = up - value;
                }
                yTra(-tmp_up);
                mGLView.translateM(0, -tmp_up);
                //yTra(-0.1f);
                //mGLView.getRender().translateM(0, -0.1f);
                mGLView.requestRender();
                break;
            case R.id.translate_up_bt:
                float tmp_down = 0.1f;
                if (down > (-value-0.1)) {
                    tmp_down = -value - down;
                }
                yTra(tmp_down);
                mGLView.translateM(0, tmp_down);
                //yTra(0.1f);
                //mGLView.getRender().translateM(0, 0.1f);
                mGLView.requestRender();
                break;
            case R.id.translate_left_bt:
                float tmp_right = 0.1f;
                if (right < (value+0.1f)) {
                    tmp_right = right - value;
                }
                xTra(-tmp_right);
                mGLView.translateM(-tmp_right, 0);
                //xTra(-0.1f);
                //mGLView.getRender().translateM(-0.1f, 0);
                mGLView.requestRender();
                break;
            case R.id.translate_right_bt:
                float tmp_left = 0.1f;
                if (left > -(value+0.1f)) {
                    tmp_left = -value - left;
                }
                xTra(tmp_left);
                mGLView.translateM(tmp_left, 0);
                //xTra(0.1f);
                //mGLView.getRender().translateM(0.1f, 0);
                mGLView.requestRender();
                break;
        }
    }

    private void loglrub() {
        Log.d(TAG, "[left]" + left + "[right]" + right + "[up]" + up + "[down]" + down);
    }

    private void updateView() {
        scale_tv.setText(String.valueOf(targetScale));
        left_tv.setText(String.valueOf(left));
        right_tv.setText(String.valueOf(right));
        up_tv.setText(String.valueOf(up));
        down_tv.setText(String.valueOf(down));
    }


    DecimalFormat df = new DecimalFormat("#.00");


    private void scaleCaclu(float targetScale) {
        left = Float.parseFloat(df.format(default_left * targetScale + centerX));
        right = Float.parseFloat(df.format(default_right * targetScale + centerX));
        up = Float.parseFloat(df.format(default_up * targetScale + centerY));
        down = Float.parseFloat(df.format(default_down * targetScale + centerY));
    }


    private void xTra(float offset) {
        centerX = centerX + offset;
        scaleCaclu(targetScale);
        updateView();
    }

    private void yTra(float offset) {
        centerY = centerY + offset;
        scaleCaclu(targetScale);
        updateView();
    }

}
