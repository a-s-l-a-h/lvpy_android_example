# lvpy_android_example

<p align="center">
  <img src="https://github.com/user-attachments/assets/6c8a522a-596c-4eda-bea9-24144ea3c445" width="300">
</p>

This repository shows how to use **lvpy** inside an **Android Studio project** using **Chaquopy**.

The `lvpy` Python package must be built as an **Android wheel (.whl)** first, then placed inside the project's `libs/` directory so Chaquopy can install it during build.

The wheel should be generated from the repository below:

https://github.com/a-s-l-a-h/lvpy_android_setup

⚠️ Use **tag `v0.04`** when building the wheel for this demo.

---

# Build the lvpy Wheel

Clone the lvpy Android build repository:

```bash
git clone https://github.com/a-s-l-a-h/lvpy_android_setup
cd lvpy_android_setup
git checkout v0.04
````

Follow the instructions in that repository to build the Android wheels.

After building, you will get files similar to:

```
lvpy-1.0-cp312-cp312-android_*.whl
```

---

# Copy the Wheel into the Android Project

Create a `libs` folder inside the Android Studio project (if it doesn't exist):

```
app/libs/
```

Copy the generated `.whl` files into:

```
app/libs/
```

Example:

```
app/libs/lvpy-1.0-cp312-cp312-android_arm64_v8a.whl
```

---

# Chaquopy Configuration

Make sure **Chaquopy uses the same Python version** used to build the wheel.

Example `build.gradle` configuration:

```gradle
chaquopy {
    defaultConfig {

        // Must match the Python version used when building the wheel
        version = "3.12" //Chaquopy version

        // Local Python interpreter location.
        // The Python version must match the Chaquopy version above.
        buildPython("/usr/bin/python3")

        pip {

            // Tell pip to search the local libs directory for wheels
            options("--find-links", "${project.projectDir}/libs")

            // Install the lvpy package from the local wheel
            install("lvpy==1.0")
        }
    }
}
```

---

# Important Notes

### 1. Python Version Must Match

Chaquopy requires the **same Python version used to build the wheel**.

Example:

```
Python used for wheel build : 3.12
Chaquopy version            : 3.12
buildPython() interpreter   : Python 3.12
```

If versions mismatch, the build will fail.

---

### 2. Wheel Location

All `.whl` files must be placed inside:

```
app/libs/
```

Chaquopy will automatically select the correct ABI wheel during build.

---

# Project Structure

Example:

```
project-root
│
├── app
│   ├── libs
│   │   └── lvpy-1.0-cp312-cp312-android_arm64_v8a.whl
│   │
│   └── build.gradle
│
└── README.md
```

---

# Workflow Summary

1. Build wheels from:

```
https://github.com/a-s-l-a-h/lvpy_android_setup
(tag v0.04)
```

2. Copy generated `.whl` files to:

```
app/libs/
```

3. Configure Chaquopy to use the **same Python version**.

4. Build the Android project.

---
