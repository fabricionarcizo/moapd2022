/*
 * MIT License
 *
 * Copyright (c) 2022 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.camerax

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dk.itu.moapd.camerax.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * An activity class with several methods to manage the main activity of CameraX application.
 */
class MainActivity : AppCompatActivity() {

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Defines a directory to save the captured images.
     */
    private lateinit var outputDirectory: File

    /**
     * An `Executor` that provides methods to manage termination and methods that can produce a
     * `Future` for tracking progress of one or more asynchronous tasks.
     */
    private lateinit var cameraExecutor: ExecutorService

    /**
     * This instance provides `takePicture()` functions to take a picture to memory or save to a
     * file, and provides image metadata.
     */
    private var imageCapture: ImageCapture? = null

    /**
     * The latest image taken by the video device stream.
     */
    private var imageUri: Uri? = null

    /**
     * The camera selector allows to select a camera or return a filtered set of cameras.
     */
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * `setContentView(int)` to inflate the activity's UI, using `findViewById()` to
     * programmatically interact with widgets in the UI, calling
     * `managedQuery(android.net.Uri, String[], String, String[], String)` to retrieve cursors for
     * data being displayed, etc.
     *
     * You can call `finish()` from within this function, in which case `onDestroy()` will be
     * immediately called after `onCreate()` without any of the rest of the activity lifecycle
     * (`onStart()`, `onResume()`, onPause()`, etc) executing.
     *
     * <em>Derived classes must call through to the super class's implementation of this method. If
     * they do not, an exception will be thrown.</em>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in `onSaveInstanceState()`.
     * <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Request camera permissions.
        if (allPermissionsGranted())
            startCamera()
        else
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

        // Define the UI behavior.
        with (binding) {

            // Set up the listener for take photo button.
            cameraCaptureButton.setOnClickListener {
                takePhoto()
            }

            // Set up the listener for the change camera button.
            cameraSwitchButton.let {
                // Disable the button until the camera is set up
                it.isEnabled = false

                // Listener for button used to switch cameras. Only called if the button is enabled
                it.setOnClickListener {
                    cameraSelector = if (CameraSelector.DEFAULT_FRONT_CAMERA == cameraSelector)
                        CameraSelector.DEFAULT_BACK_CAMERA
                    else
                        CameraSelector.DEFAULT_FRONT_CAMERA

                    // Re-start use cases to update selected camera.
                    startCamera()
                }
            }

            // Set up the listener for the photo view button.
            photoViewButton.setOnClickListener {
                if (imageUri != null) {
                    val intent = Intent(baseContext, PhotoActivity::class.java).apply {
                        putExtra("uri", imageUri.toString())
                    }
                    startActivity(intent)
                }
            }
        }

        // Create and get the output directory.
        outputDirectory = getOutputDirectory()

        // Create an Executor that uses a single worker thread operating off an unbounded queue.
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Inflate the user interface into the current activity.
        setContentView(binding.root)
    }

    /**
     * Perform any final cleanup before an activity is destroyed. This can happen either because the
     * activity is finishing (someone called `finish()` on it), or because the system is temporarily
     * destroying this instance of the activity to save space. You can distinguish between these two
     * scenarios with the `isFinishing()` method.
     *
     * Note: do not count on this method being called as a place for saving data! For example, if an
     * activity is editing data in a content provider, those edits should be committed in either
     * `onPause()` or `onSaveInstanceState()`, not here. This method is usually implemented to free
     * resources like threads that are associated with an activity, so that a destroyed activity
     * does not leave such things around while the rest of its application is still running. There
     * are situations where the system will simply kill the activity's hosting process without
     * calling this method (or any others) in it, so it should not be used to do things that are
     * intended to remain around after the process goes away.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on
     * `requestPermissions(String[], int)`.
     *
     * It is possible that the permissions request interaction with the user is interrupted. In this
     * case you will receive empty permissions and results arrays which should be treated as a
     * cancellation.
     *
     * @param requestCode The request code passed in `requestPermissions(String[], int)`.
     * @param permissions The requested permissions. Never `null`.
     * @param grantResults The grant results for the corresponding permissions which is either
     *      `android.content.pm.PackageManager#PERMISSION_GRANTED` or
     *      `android.content.pm.PackageManager#PERMISSION_DENIED}`. Never null.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Check if the user has accepted the permissions to access the camera.
        if (requestCode == REQUEST_CODE_PERMISSIONS)
            if (allPermissionsGranted())
                startCamera()

            // If permissions are not granted, present a toast to notify the user that the
            // permissions were not granted.
            else {
                toast("Permissions not granted by the user.")
                finish()
            }
    }

    /**
     * A method to show a dialog to the users as ask permission to access their Android mobile
     * device resources.
     *
     * @return `PackageManager#PERMISSION_GRANTED` if the given pid/uid is allowed that permission,
     *      or `PackageManager#PERMISSION_DENIED` if it is not.
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * This method is used to start the video camera device stream.
     */
    private fun startCamera() {
        // Create an instance of the `ProcessCameraProvider` to bind the lifecycle of cameras to the
        // lifecycle owner.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // Add a listener to the `cameraProviderFuture`.
        cameraProviderFuture.addListener({

            // Used to bind the lifecycle of cameras to the lifecycle owner.
            val cameraProvider = cameraProviderFuture.get()

            // Video camera streaming preview.
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            // Set up the image capture by getting a reference to the `ImageCapture`.
            imageCapture = ImageCapture.Builder().build()

            try {
                // Unbind use cases before rebinding.
                cameraProvider.unbindAll()

                // Bind use cases to camera.
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

                // Update the camera switch button.
                updateCameraSwitchButton(cameraProvider)

            } catch(ex: Exception) {
                Log.e(TAG, "Use case binding failed", ex)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * This method is used to save a frame from the video camera device stream as a JPG photo.
     */
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case.
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image.
        val photoFile = File(outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata.
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken.
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                /**
                 * Called when an image has been successfully saved.
                 */
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imageUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $imageUri"
                    toast(msg)
                    Log.d(TAG, msg)
                }

                /**
                 * Called when an error occurs while attempting to save an image.
                 *
                 * @param exception An `ImageCaptureException` that contains the type of error, the
                 *      error message and the throwable that caused it.
                 */
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    /**
     * Enabled or disabled a button to switch cameras depending on the available cameras.
     */
    private fun updateCameraSwitchButton(provider: ProcessCameraProvider) {
        try {
            binding.cameraSwitchButton.isEnabled =
                hasBackCamera(provider) && hasFrontCamera(provider)
        } catch (exception: CameraInfoUnavailableException) {
            binding.cameraSwitchButton.isEnabled = false
        }
    }

    /**
     * Returns true if the device has an available back camera. False otherwise.
     */
    private fun hasBackCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)

    /**
     * Returns true if the device has an available front camera. False otherwise.
     */
    private fun hasFrontCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)

    /**
     * This method checks, creates and returns the output directory where the captured image will be
     * saved.
     *
     * @return A full path where the image will be saved.
     */
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    /**
     * Make a standard toast that just contains text.
     *
     * @param text The text to show. Can be formatted text.
     * @param duration How long to display the message. Either `Toast.LENGTH_SHORT` or
     *      `Toast.LENGTH_LONG`.
     */
    private fun toast(text: CharSequence,
                      duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, duration).show()
    }
}
