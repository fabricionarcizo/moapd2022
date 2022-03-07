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
package dk.itu.moapd.storagefile

import android.content.Context
import android.util.Log
import java.io.*

/**
 * An utility class to manage and persist application data in a text file.
 */
class FileUtils(private val context: Context) {

    /**
     * A set of static attributes used in this class.
     */
    companion object {
        private val TAG = FileUtils::class.qualifiedName
        private const val filename = "data.txt"
    }

    /**
     * Write application data in a text file.
     *
     * @param data The data to be written in the dataset.
     */
    fun writeDataToFile(data: String) {
        if (data.isEmpty())
            return

        try {
            // Open a private file associated with this Context's application package for writing.
            val fileOutputStream =
                context.openFileOutput(filename, Context.MODE_APPEND)

            // Convert the input data into bytes.
            val dataInBytes = data.toByteArray()
            val lineSeparator = System.getProperty("line.separator")

            // Write the input data in the text file.
            fileOutputStream?.write(dataInBytes)
            fileOutputStream?.write(
                lineSeparator?.toByteArray()!!)

            // Release the text file resources.
            fileOutputStream?.flush()
            fileOutputStream?.close()

        } catch (ex: IOException) {
            Log.e(TAG, ex.message, ex)
        }
    }

    /**
     * Read application data from a text file.
     *
     * @return A list with each line read from a text file.
     */
    fun readDataFromFile(): ArrayList<String> {
        val data = ArrayList<String>()

        try {
            // Open a private file associated with this Context's application package for reading.
            val fileInputStream =
                context.openFileInput(filename) as FileInputStream

            // Creates an InputStreamReader that uses the default charset.
            val inputStreamReader = InputStreamReader(fileInputStream)

            // Creates a buffering character-input stream that uses a default-sized input buffer.
            val bufferedReader = BufferedReader(inputStreamReader)

            // Read all lines from the text file.
            var lineData = bufferedReader.readLine()
            while (lineData != null) {
                data.add(lineData)
                lineData = bufferedReader.readLine()
            }

            // Release the text file resources.
            bufferedReader.close()
            inputStreamReader.close()
            fileInputStream.close()

        } catch (ex: IOException) {
            Log.e(TAG, ex.message, ex)
        }

        // Return the read data.
        return data
    }

    /**
     * Delete application data from a text file.
     *
     * @param data The current data structure shown in the `RecyclerView`.
     * @param position The number of the line to be removed from the text file.
     */
    fun removeDataFromFile(data: ArrayList<String>, position: Int) {
        try {

            // Open an empty file associated with this Context's application package for writing.
            val fileOutputStream =
                context.openFileOutput(filename, Context.MODE_PRIVATE)
            val lineSeparator = System.getProperty("line.separator")

            // Write again all items in the text file.
            for (i in data.indices) {

                // Don't write the item to be removed.
                if (i == position)
                    continue

                // Write the item in the text file.
                fileOutputStream?.write(data[i].toByteArray())
                fileOutputStream?.write(
                    lineSeparator?.toByteArray()!!)
            }

            // Release the text file resources.
            fileOutputStream?.flush()
            fileOutputStream?.close()

        } catch (ex: IOException) {
            Log.e(TAG, ex.message, ex)
        }
    }

}
