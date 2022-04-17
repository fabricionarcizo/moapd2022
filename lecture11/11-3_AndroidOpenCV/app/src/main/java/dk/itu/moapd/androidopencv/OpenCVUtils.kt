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

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

/**
 * An utility class to manage a set of image analysis algorithms.
 */
class OpenCVUtils {

    /**
     * A set of static attributes used in this class.
     */
    companion object {

        /**
         * Convert the input image to a grayscale image.
         *
         * @param image The input image in the RGBA color space.
         *
         * @return A transformed image in the grayscale color space.
         */
        fun convertToGrayscale(image: Mat?): Mat {
            val grayscale = Mat()
            Imgproc.cvtColor(image, grayscale, Imgproc.COLOR_RGBA2GRAY)
            return grayscale
        }

        /**
         * Convert the input image to a BGRA image.
         *
         * @param image The input image in the RGBA color space.
         *
         * @return A transformed image in the BGRA color space.
         */
        fun convertToBgra(image: Mat?): Mat {
            val bgra = Mat()
            Imgproc.cvtColor(image, bgra, Imgproc.COLOR_RGBA2BGRA)
            return bgra
        }

        /**
         * Apply a Canny filter in the input image.
         *
         * @param image The input image in the RGBA color space.
         *
         * @return A filtered image with a Canny filter.
         */
        fun convertToCanny(image: Mat?): Mat {

            // Canny filter requires a grayscale image as the input image.
            val grayscale = convertToGrayscale(image)

            // Apply an automatic threshold to create a binary image.
            val thresh = Mat()
            val otsuThresh = Imgproc.threshold(grayscale, thresh,
                0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)

            // Apply the Canny filter.
            val canny = Mat()
            Imgproc.Canny(grayscale, canny, otsuThresh * 0.5, otsuThresh)

            // Release the unused OpenCV resources.
            grayscale.release()
            thresh.release()

            // Return the filtered image.
            return canny
        }

    }

}
