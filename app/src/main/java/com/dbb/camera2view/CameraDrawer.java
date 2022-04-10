package com.dbb.camera2view;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Display Draw
 */
public class CameraDrawer {

    protected float[] scaleMatrix = MatrixUtils.getOriginalMatrix();
    protected float[] translateMatrix = MatrixUtils.getOriginalMatrix();

    private final int mProgram;
    private final int mPositionHandle;
    private final int mTextureHandle;
    private final int glHMatrix;

    private final FloatBuffer bPos;
    private final FloatBuffer bCoord;
    private final float value = 1.0f;

    private final float[] sPos = {
            -value, value,
            -value, -value,
            value, value,
            value, -value
    };


    public void scale(float scale) {
        sPos[0] = -value * scale;
        sPos[1] = value * scale;
        sPos[2] = -value * scale;
        sPos[3] = -value * scale;
        sPos[4] = value * scale;
        sPos[5] = value * scale;
        sPos[6] = value * scale;
        sPos[7] = -value * scale;
        bPos.clear();
        bPos.put(sPos);
        bPos.position(0);
    }

    public void translateM(float x, float y) {
        Matrix.translateM(translateMatrix, 0, x, y, 0);
    }

    protected float[] mMVPMatrix = new float[16];

    public CameraDrawer() {

        ByteBuffer bb = ByteBuffer.allocateDirect(sPos.length * 4);
        bb.order(ByteOrder.nativeOrder());
        bPos = bb.asFloatBuffer();
        bPos.put(sPos);

        bPos.position(0);
        float[] sCoord = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };
        ByteBuffer cc = ByteBuffer.allocateDirect(sCoord.length * 4);
        cc.order(ByteOrder.nativeOrder());
        bCoord = cc.asFloatBuffer();
        bCoord.put(sCoord);
        bCoord.position(0);


        String VERTEX_SHADER = "" +
                "attribute vec4 vPosition;" +
                "attribute vec2 inputTextureCoordinate;" +
                "varying vec2 textureCoordinate;" +
                "uniform mat4 vMatrix;" +
                "void main()" +
                "{" +
                "gl_Position = vMatrix*vPosition;" +
                "textureCoordinate = inputTextureCoordinate;" +
                "}";
        String FRAGMENT_SHADER = "" +
                "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;" +
                "varying vec2 textureCoordinate;\n" +
                "uniform samplerExternalOES s_texture;\n" +
                "void main() {" +
                "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
                "}";
        mProgram = OpenGLUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        glHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");

        float[] mProjectMatrix = new float[16];
        Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1, 1, 3, 5);
        float[] mViewMatrix = new float[16];
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

    }

    public void draw(int texture) {
        GLES20.glUseProgram(mProgram); // 指定使用的program
        GLES20.glEnable(GLES20.GL_CULL_FACE); // 启动剔除
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture); // 绑定纹理

        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, mMVPMatrix, 0, translateMatrix, 0);
        float[] temp1 = new float[16];
        Matrix.multiplyMM(temp1, 0, scaleMatrix, 0, temp, 0);
        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, temp1, 0);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, bPos);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, bCoord);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
    }
}
