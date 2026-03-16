package com.example.lvpy_android_example;

import android.opengl.GLSurfaceView;
import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LvPyRenderer implements GLSurfaceView.Renderer {

    // JNI bindings (Updated to pass float dpi)
    private native void nativeSurfaceCreated();
    private native void nativeInit(int width, int height, float dpi);
    private native void nativeDrawFrame();
    private native void nativeResize(int width, int height, float dpi);
    private native void nativeDestroy();

    private PyObject pyModule    = null;
    private boolean  lvglReady   = false;
    private boolean  pythonReady = false;

    private float screenDpi;

    public LvPyRenderer(float dpi) {
        this.screenDpi = dpi;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        nativeSurfaceCreated();
        lvglReady = false;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 1. Initialize C++ LVGL screen first! (Pass DPI)
        nativeInit(width, height, screenDpi);
        lvglReady = true;

        // 2. NOW evaluate the user's Python script.
        if (!pythonReady) {
            pyModule = Python.getInstance().getModule("main");
            pythonReady = true;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!lvglReady) return;
        nativeDrawFrame();
    }

    public void destroy() {
        if (lvglReady) {
            nativeDestroy();
            lvglReady = false;
        }
    }
}