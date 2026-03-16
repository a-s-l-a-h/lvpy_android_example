package com.example.lvpy_android_example;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class LvPyGLView extends GLSurfaceView {

    // Updated Signature: Added int id
    private native void nativePushTouch(int id, int action, float x, float y);
    private native void nativePushKey(int keyCode, int action);
    private native void nativePushText(int codepoint);

    public LvPyGLView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        // Fetch physical DPI
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dpi = metrics.densityDpi; // Can also use metrics.xdpi

        setRenderer(new LvPyRenderer(dpi));
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    /* ── Touch (UPDATED FOR MULTITOUCH) ────────────────────────── */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);

        // Limit to 5 fingers to match C++ backend
        if (pointerId >= 5) return true;

        int actionType = -1; // 0=down, 1=up, 2=move
        if (actionMasked == MotionEvent.ACTION_DOWN || actionMasked == MotionEvent.ACTION_POINTER_DOWN) actionType = 0;
        else if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_POINTER_UP || actionMasked == MotionEvent.ACTION_CANCEL) actionType = 1;
        else if (actionMasked == MotionEvent.ACTION_MOVE) actionType = 2;

        if (actionType == 2) {
            // MOVE triggers for all fingers simultaneously, we must loop through them
            for (int i = 0; i < event.getPointerCount(); i++) {
                int id = event.getPointerId(i);
                if (id < 5) {
                    nativePushTouch(id, 2, event.getX(i), event.getY(i));
                }
            }
        } else if (actionType != -1) {
            // DOWN / UP trigger for specific pointer
            nativePushTouch(pointerId, actionType, event.getX(pointerIndex), event.getY(pointerIndex));
        }
        return true;
    }

    /* ── Hardware keys ─────────────────────────────────────────── */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        nativePushKey(keyCode, 1);
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        nativePushKey(keyCode, 0);
        return true;
    }

    /* ── Soft keyboard — InputConnection ───────────────────────── */
    @Override
    public boolean onCheckIsTextEditor() { return true; }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo info) {
        info.inputType = android.text.InputType.TYPE_CLASS_TEXT;
        return new BaseInputConnection(this, false) {
            @Override
            public boolean commitText(CharSequence text, int newCursorPos) {
                for (int i = 0; i < text.length(); ) {
                    int cp = Character.codePointAt(text, i);
                    nativePushText(cp);
                    i += Character.charCount(cp);
                }
                return true;
            }
            @Override
            public boolean deleteSurroundingText(int before, int after) {
                for (int i = 0; i < before; i++) nativePushKey(67, 1);
                return true;
            }
        };
    }
}