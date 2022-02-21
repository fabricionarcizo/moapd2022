/*
 * MIT License
 *
 * Copyright (c) 2021 Fabricio Batista Narcizo
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
package dk.itu.moapd.dialogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * An activity class to show a confirmation dialog to the user.
 */
class ConfirmationActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_confirmation)

        val singleItems = arrayOf("First Item", "Second Item", "Third Item")
        val checkedItem = 1

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.confirmation_title))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                // TODO: Respond to neutral button press
                toast("Cancelled")
            }
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                // TODO: Respond to positive button press
                toast("Confirmed")
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                // TODO: Respond to item chosen
                toast(singleItems[which])
            }
            .show()
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