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
package dk.itu.moapd.opencvml

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgproc.Imgproc
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * An utility class to manage a set of image analysis algorithms.
 */
class OpenCVUtils(context: Context) {

    /**
     * An instance to create and manipulate comprehensive artificial neural networks.
     */
    private var net: Net

    /**
     * A set of static attributes used in this class.
     */
    companion object {
        private val TAG = OpenCVUtils::class.qualifiedName
        private val CLASSES = arrayOf("background",
            "aeroplane", "bicycle", "bird", "boat",
            "bottle", "bus", "car", "cat", "chair",
            "cow", "diningtable", "dog", "horse",
            "motorbike", "person", "pottedplant",
            "sheep", "sofa", "train", "tvmonitor"
        )
    }

    /**
     * Initializes the OpenCVUtils instance.
     */
    init {
        val proto: String =
            getPath("MobileNetSSD_deploy.prototxt", context)
        val weights: String =
            getPath("MobileNetSSD_deploy.caffemodel", context)

        net = Dnn.readNetFromCaffe(proto, weights)
    }

    /**
     * Try to detects an object in the input image.
     *
     * @param image The input image in the RGBA color space.
     * @param width The minimum width of the detected object.
     * @param height The minimum height of the detected object.
     * @param scaleFactor The scale factor used in the pyramids approach used by the neural network.
     * @param mean The mean scalar.
     * @param threshold The minimum confidence threshold.
     *
     * @return An image with the detected objects drawn in rectangles.
     */
    fun objectDetection(image: Mat?, width: Double = 300.0, height: Double = 300.0,
                        scaleFactor: Double = 0.007843, mean: Double = 127.5,
                        threshold: Double = 0.2): Mat {

        // Convert the image from RGBA to BGRA.
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB)

        // Forward image through network.
        val blob = Dnn.blobFromImage(image, scaleFactor,
            Size(width, height),
            Scalar(mean, mean, mean),
            /*swapRB*/ false, /*crop*/false
        )

        // Sets the new input value for the network.
        net.setInput(blob)
        var detections = net.forward()

        // Process the detected objects.
        detections = detections.reshape(1, detections.total().toInt() / 7)
        for (i in 0 until detections.rows()) {
            // Evaluate the threshold of current object.
            val confidence = detections[i, 2][0]
            if (confidence > threshold)
                drawRectangles(image, detections, i, confidence)
        }

        return image!!
    }

    /**
     * Draw rectangles around the detected objects in the input image.
     *
     * @param image The input image in the RGBA color space.
     * @param detections A data structure with information about the detected objects.
     * @param index The index of the current detected object.
     * @param confidence The confidence of current detected object.
     */
    private fun drawRectangles(image: Mat?, detections: Mat, index: Int, confidence: Double) {

        // Get the input image resolution.
        val cols = image?.cols()
        val rows = image?.rows()

        // Get the class ID.
        val classId = detections[index, 1][0].toInt()

        // Define the rectangle corners.
        val left = (detections[index, 3][0] * cols!!)
        val top = (detections[index, 4][0] * rows!!)
        val right = (detections[index, 5][0] * cols)
        val bottom = (detections[index, 6][0] * rows)

        // Draw rectangle around detected object.
        Imgproc.rectangle(image, Point(left, top),
            Point(right, bottom), Scalar(0.0, 255.0, 0.0), 3)

        // Draw the class name as a text.
        val label = CLASSES[classId] + ": " + confidence
        val baseLine = IntArray(1)
        val labelSize: Size = Imgproc.getTextSize(label,
            Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, 2, baseLine)

        // Draw background for label.
        Imgproc.rectangle(image,
            Point(left, top - labelSize.height),
            Point(left + labelSize.width, top + baseLine[0]),
            Scalar(255.0, 255.0, 255.0), Imgproc.FILLED
        )

        // Write class name and confidence.
        Imgproc.putText(image, label, Point(left, top),
            Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, Scalar(0.0, 0.0, 0.0)
        )
    }

    /**
     * Get the path of files with the trained neural network.
     *
     * @param file The filename of the trained neural network.
     * @param context The current context application.
     *
     * @return The full path of trained neural network.
     */
    private fun getPath(file: String, context: Context): String {
        val assetManager: AssetManager = context.assets
        val inputStream: BufferedInputStream

        try {

            // Read data from assets.
            inputStream = BufferedInputStream(assetManager.open(file))
            val data = ByteArray(inputStream.available())
            inputStream.read(data)
            inputStream.close()

            // Create copy file in storage.
            val outFile = File(context.filesDir, file)
            val os = FileOutputStream(outFile)
            os.write(data)
            os.close()

            // Return a path to file which may be read in common way.
            return outFile.absolutePath

        } catch (ex: IOException) {
            Log.i(TAG, "Failed to upload a file")
        }

        return ""
    }

}
