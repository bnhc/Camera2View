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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;


/**
 * Description: 测试GLSurfaceView
 * @author 大博博
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
    private final float yValue =1.9f;//条形显示

    private final float default_left = -value;
    private final float default_right = value;
    private final float default_up = yValue;
    private final float default_down = -yValue;

    private float left = default_left, right = default_right, up = default_up, down = default_down;
    private float centerX = 0f, centerY = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        initView();
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
        updateLRUDStatus();
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

    private float targetScale = value;

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scale_large_bt:
                targetScale = targetScale + 0.1f;
                scaleCalculate(targetScale);
                updateLRUDStatus();
                if (targetScale > 4.0f) targetScale = 4.0f;
                mGLView.scale(targetScale);
                mGLView.requestRender();
                break;
            case R.id.scale_small_bt:
                targetScale = targetScale - 0.1f;
                if (targetScale < 1.0f) targetScale = 1.0f;
                scaleCalculate(targetScale);
                mGLView.scale(targetScale);
                if (left > -value) {
                    float left_offset = -value - left;
                    xMove(left_offset);
                    mGLView.translateM(left_offset, 0);
                }
                if (right < value) {
                    float right_offset = value - right;
                    xMove(right_offset);
                    mGLView.translateM(right_offset, 0);
                }
                if (up < value) {
                    float up_offset = value - up;
                    yMove(up_offset);
                    mGLView.translateM(0, up_offset);
                }
                if (down > -value) {
                    float down_offset = -value - down;
                    yMove(down_offset);
                    mGLView.translateM(0, down_offset);
                }
                mGLView.requestRender();
                updateLRUDStatus();
                break;
            case R.id.translate_down_bt:
                float tmp_up = 0.1f;
                if (up < (value + 0.1)) {
                    tmp_up = up - value;
                }
                yMove(-tmp_up);
                mGLView.translateM(0, -tmp_up);
                mGLView.requestRender();
                break;
            case R.id.translate_up_bt:
                float tmp_down = 0.1f;
                if (down > (-value - 0.1)) {
                    tmp_down = -value - down;
                }
                yMove(tmp_down);
                mGLView.translateM(0, tmp_down);
                mGLView.requestRender();
                break;
            case R.id.translate_left_bt:
                float tmp_right = 0.1f;
                if (right < (value + 0.1f)) {
                    tmp_right = right - value;
                }
                xMove(-tmp_right);
                mGLView.translateM(-tmp_right, 0);
                mGLView.requestRender();
                break;
            case R.id.translate_right_bt:
                float tmp_left = 0.1f;
                if (left > -(value + 0.1f)) {
                    tmp_left = -value - left;
                }
                xMove(tmp_left);
                mGLView.translateM(tmp_left, 0);
                mGLView.requestRender();
                break;
        }
    }

    private void loglrub() {
        Log.d(TAG, "[left]" + left + "[right]" + right + "[up]" + up + "[down]" + down);
    }

    /**
     * 更新left,right,up,down的数字
     */
    private void updateLRUDStatus() {
        scale_tv.setText(String.valueOf(targetScale));
        left_tv.setText(String.valueOf(left));
        right_tv.setText(String.valueOf(right));
        up_tv.setText(String.valueOf(up));
        down_tv.setText(String.valueOf(down));
    }


    private final DecimalFormat df = new DecimalFormat("#.00");

    /**
     * 计算放大/缩小 left,right,up,down的坐标
     *
     * @param targetScale 缩放比例
     */
    private void scaleCalculate(float targetScale) {
        left = Float.parseFloat(df.format(default_left * targetScale + centerX));
        right = Float.parseFloat(df.format(default_right * targetScale + centerX));
        up = Float.parseFloat(df.format(default_up * targetScale + centerY));
        down = Float.parseFloat(df.format(default_down * targetScale + centerY));
    }

    /**
     * x轴平移
     *
     * @param offset like 0.1f(right) or -0.1f(left)
     */
    private void xMove(float offset) {
        centerX = centerX + offset;
        scaleCalculate(targetScale);
        updateLRUDStatus();
    }

    /**
     * y轴平移
     *
     * @param offset 0.1f(up) or -0.1f(down)
     */
    private void yMove(float offset) {
        centerY = centerY + offset;
        scaleCalculate(targetScale);
        updateLRUDStatus();
    }

}
