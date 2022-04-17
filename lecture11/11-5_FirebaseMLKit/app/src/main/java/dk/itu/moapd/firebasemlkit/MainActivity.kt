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
package dk.itu.moapd.firebasemlkit

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import dk.itu.moapd.firebasemlkit.databinding.ActivityMainBinding
import kotlin.math.min

/**
 * An activity class with methods to manage the main activity of Firebase ML Kit application.
 */
class MainActivity : AppCompatActivity() {

    /**
     * This object launches a new activity and receives back some result data.
     */
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        galleryResult(result)
    }

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * The URI of latest taken photo using the camera intent.
     */
    private lateinit var capturedImageUri: Uri

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private const val MAX_FONT_SIZE = 96F
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

        // Define the UI behavior.
        with (binding) {

            // Listener for button used to take photos.
            captureButton.setOnClickListener {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                    intent.resolveActivity(packageManager)?.also {

                        // Create an implicit intent to capture photos.
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "ML Kit")
                        capturedImageUri = contentResolver
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
                        cameraLauncher.launch(intent)
                    }
                }
            }
        }

        // Inflate the user interface into the current activity.
        setContentView(binding.root)
    }

    /**
     * When the second activity finishes (i.e., the take photo intent), it returns a result to this
     * activity. If the user selects an image correctly, we can get a reference of the selected
     * image and send it to the object detection method.
     *
     * @param result A container for an activity result as obtained form `onActivityResult()`.
     */
    private fun galleryResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {

            // Get the captured image as a `Bitmap`.
            val image = getCapturedImage()

            // Display capture image.
            binding.imageView.setImageBitmap(image)

            // Run through ODT and display result.
            runObjectDetection(image)
        }
    }

    /**
     * Get the latest captured image as a `Bitmap`.
     *
     * @return The bitmap of latest captured image.
     */
    private fun getCapturedImage(): Bitmap {

        // Open an image available in the media store.
        val bitmap = InputImage.fromFilePath(this, capturedImageUri).bitmapInternal!!

        // Crop image to match imageView's aspect ratio.
        val scaleFactor = min(
            bitmap.width / binding.imageView.width.toFloat(),
            bitmap.height / binding.imageView.height.toFloat()
        )

        val deltaWidth = (bitmap.width - binding.imageView.width * scaleFactor).toInt()
        val deltaHeight = (bitmap.height - binding.imageView.height * scaleFactor).toInt()

        // Scale the image to the imageView size.
        val scaledImage = Bitmap.createBitmap(
            bitmap, deltaWidth / 2, deltaHeight / 2,
            bitmap.width - deltaWidth, bitmap.height - deltaHeight
        )

        // Return the scaled image.
        bitmap.recycle()
        return scaledImage
    }

    /**
     * ML Kit Object Detection function.
     *
     * @param bitmap The input image with objects to be detected.
     */
    private fun runObjectDetection(bitmap: Bitmap) {
        // Step 1: Create ML Kit's InputImage object.
        val image = InputImage.fromBitmap(bitmap, 0)

        // Step 2: Acquire detector object.
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        val objectDetector = ObjectDetection.getClient(options)

        // Step 3: Feed given image to detector and setup callback.
        objectDetector.process(image).addOnSuccessListener { results ->
            // TODO: Task completed successfully.
            // Parse ML Kit's DetectedObject and create corresponding visualization data.
            val detectedObjects = results.map {
                var text = "Unknown"

                // We will show the top confident detection result if it exist.
                if (it.labels.isNotEmpty()) {
                    val firstLabel = it.labels.first()
                    text = "${firstLabel.text}, ${firstLabel.confidence.times(100).toInt()}%"
                }
                BoxWithText(it.boundingBox, text)
            }

            // Draw the detection result on the input bitmap.
            val visualizedResult = drawDetectionResult(bitmap, detectedObjects)

            // Show the detection result on the app screen.
            binding.imageView.setImageBitmap(visualizedResult)

        }.addOnFailureListener {
            // TODO: Task failed with an exception.
            Log.e(TAG, it.message.toString())
        }
    }

    /**
     * Draw bounding boxes around objects together with the object's name.
     *
     * @param bitmap The input image to detect objects.
     * @param detectionResults A list of objects detected in the input image.
     *
     * @return An image with the detected objects around rectangles.
     */
    private fun drawDetectionResult(bitmap: Bitmap, detectionResults: List<BoxWithText>): Bitmap {

        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // Draw bounding box.
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.box
            canvas.drawRect(box, pen)

            val tagSize = Rect(0, 0, 0, 0)

            // Calculate the right font size.
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // Adjust the font size so texts are inside the bounding box.
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )

        }
        return outputBitmap
    }

}

/**
 * A general-purpose data class to store detection result for visualization
 */
data class BoxWithText(val box: Rect, val text: String)
