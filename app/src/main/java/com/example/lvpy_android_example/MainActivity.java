package com.example.lvpy_android_example;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.lvpybackend.NativeBridge;

public class MainActivity extends AppCompatActivity {

    private LvPyGLView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Start Python
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        // 2. Safely get the .so path WITHOUT importing main.py.
        // We use an inline script to grab the path from the wheel extraction cache.
        String soPath = null;
        try {
            Python py = Python.getInstance();
            PyObject builtins = py.getModule("builtins");
            PyObject globals = builtins.callAttr("dict");

            String inlineScript = "import lvpy\npath = lvpy.__file__";
            builtins.callAttr("exec", inlineScript, globals);

            soPath = globals.callAttr("get", "path").toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lvpy.so path", e);
        }

        // 3. System.load() so the JVM native linker sees JNI symbols
        try {
            System.load(soPath);
        } catch (UnsatisfiedLinkError e) {
            // already loaded in same classloader — safe to continue
        }

        // 4. Register native methods
        NativeBridge.registerNatives(LvPyRenderer.class, LvPyGLView.class);

        // 5. GL view takes over
        glView = new LvPyGLView(this);
        setContentView(glView);
    }

    @Override protected void onPause()   { super.onPause();   if(glView != null) glView.onPause();  }
    @Override protected void onResume()  { super.onResume();  if(glView != null) glView.onResume(); }
    @Override protected void onDestroy() { super.onDestroy(); glView = null;     }
}