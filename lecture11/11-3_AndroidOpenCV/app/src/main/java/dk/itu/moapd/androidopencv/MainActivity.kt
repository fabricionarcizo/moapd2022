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
package dk.itu.moapd.androidopencv

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dk.itu.moapd.androidopencv.databinding.ActivityMainBinding
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.CvType.CV_8UC4
import org.opencv.core.Mat

/**
 * An activity class with methods to manage the main activity of Android OpenCV application.
 */
class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * A callback from OpenCV Manager to handle the OpenCV library.
     */
    private lateinit var loaderCallback: BaseLoaderCallback

    /**
     * The OpenCV image storage.
     */
    private lateinit var imageMat: Mat

    /**
     * The camera characteristics allows to select a camera or return a filtered set of cameras.
     */
    private var cameraCharacteristics = CameraCharacteristics.LENS_FACING_BACK

    /**
     * A variable to control the image analysis method to apply in the input image.
     */
    private var currentMethodId = 0

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
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

            // Listener for button used to switch cameras.
            cameraSwitchButton.setOnClickListener {
                cameraCharacteristics =
                    if (CameraCharacteristics.LENS_FACING_FRONT == cameraCharacteristics)
                        CameraCharacteristics.LENS_FACING_BACK
                    else
                        CameraCharacteristics.LENS_FACING_FRONT

                // Re-start use cases to update selected camera.
                cameraView.disableView()
                cameraView.setCameraIndex(cameraCharacteristics)
                cameraView.enableView()
            }

            // Listener for button used to change the image analysis method.
            imageAnalysisButton.setOnClickListener {
                currentMethodId++
                currentMethodId %= 4
            }
        }

        // Inflate the user interface into the current activity.
        setContentView(binding.root)
    }

    /**
     * Called after `onStart()`, `onRestart()`, or `onPause()`, for your activity to start
     * interacting with the user. This is an indicator that the activity became active and ready to
     * receive input. It is on top of an activity stack and visible to user.
     *
     * On platform versions prior to `android.os.Build.VERSION_CODES#Q` this is also a good place to
     * try to open exclusive-access devices or to get access to singleton resources. Starting  with
     * `android.os.Build.VERSION_CODES#Q` there can be multiple resumed activities in the system
     * simultaneously, so `onTopResumedActivityChanged(boolean)` should be used for that purpose
     * instead.
     *
     * <Derived classes must call through to the super class's implementation of this method. If
     * they do not, an exception will be thrown.
     */
    override fun onResume() {
        super.onResume()
        // Try to initialize OpenCV using the newest init method. Otherwise, use the asynchronous
        // one.
        if (!OpenCVLoader.initDebug())
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,
                this, loaderCallback)
        else
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
    }

    /**
     * Called as part of the activity lifecycle when the user no longer actively interacts with the
     * activity, but it is still visible on screen. The counterpart to `onResume()`.
     *
     * When activity `B` is launched in front of activity `A`, this callback will be invoked on `A`.
     * `B` will not be created until `A`'s onPause() returns, so be sure to not do anything lengthy
     * here.
     *
     * This callback is mostly used for saving any persistent state the activity is editing, to
     * present a "edit in place" model to the user and making sure nothing is lost if there are not
     * enough resources to start the new activity without first killing this one. This is also a
     * good place to stop things that consume a noticeable amount of CPU in order to make the switch
     * to the next activity as fast as possible.
     *
     * On platform versions prior to `android.os.Build.VERSION_CODES#Q` this is also a good place to
     * try to close exclusive-access devices or to release access to singleton resources. Starting
     * with `android.os.Build.VERSION_CODES#Q` there can be multiple resumed activities in the
     * system at the same time, so `onTopResumedActivityChanged(boolean)` should be used for that
     * purpose instead.
     *
     * If an activity is launched on top, after receiving this call you will usually receive a
     * following call to `onStop()` (after the next activity has been resumed and displayed above).
     * However in some cases there will be a direct call back to `onResume()` without going through
     * the stopped state. An activity can also rest in paused state in some cases when in
     * multi-window mode, still visible to user.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onPause() {
        super.onPause()
        binding.cameraView.disableView()
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
        binding.cameraView.disableView()
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
     * This method is invoked when camera preview has started. After this method is invoked the
     * frames will start to be delivered to client via the `onCameraFrame()` callback.
     *
     * @param width The width of the frames that will be delivered.
     * @param height The height of the frames that will be delivered
     */
    override fun onCameraViewStarted(width: Int, height: Int) {
        // Create the OpenCV Mat structure to represent images in the library.
        imageMat = Mat(height, width, CV_8UC4)
    }

    /**
     * This method is invoked when camera preview has been stopped for some reason. No frames will
     * be delivered via `onCameraFrame()` callback after this method is called.
     */
    override fun onCameraViewStopped() {
        imageMat.release()
    }

    /**
     * This method is invoked when delivery of the frame needs to be done. The returned values - is
     * a modified frame which needs to be displayed on the screen.
     *
     * @param inputFrame The current frame grabbed from the video camera device stream.
     */
    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        // Get the current frame and copy it to the OpenCV Mat structure.
        val image = inputFrame?.rgba()
        imageMat = image!!

        if (cameraCharacteristics == CameraCharacteristics.LENS_FACING_BACK)
            Core.flip(image, image, 1)

        return when (currentMethodId) {
            1 -> OpenCVUtils.convertToGrayscale(image)
            2 -> OpenCVUtils.convertToBgra(image)
            3 -> OpenCVUtils.convertToCanny(image)
            else -> image
        }
    }

    /**
     * This method is used to start the video camera device stream.
     */
    private fun startCamera() {

        // Setup the OpenCV camera view.
        with (binding) {
            cameraView.visibility = SurfaceView.VISIBLE
            cameraView.setCameraIndex(cameraCharacteristics)
            cameraView.setCameraPermissionGranted()
            cameraView.setCvCameraViewListener(this@MainActivity)
        }

        // Initialize the callback from OpenCV Manager to handle the OpenCV library.
        loaderCallback = object : BaseLoaderCallback(this) {
            override fun onManagerConnected(status: Int) {
                when (status) {
                    SUCCESS -> binding.cameraView.enableView()
                    else -> super.onManagerConnected(status)
                }
            }
        }
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
