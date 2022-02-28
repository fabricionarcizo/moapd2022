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
package dk.itu.moapd.androidtesting

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.androidtesting.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Android Testing application.
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
     * A component to validate the user's name.
     */
    private lateinit var nameValidator: TextValidator

    /**
     * A component to validate the user's email.
     */
    private lateinit var emailValidator: TextValidator

    /**
     * A component to validate the user's password.
     */
    private lateinit var passwordValidator: TextValidator

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

        // Define the UI components behavior.
        with (binding) {

            // Setup field validators.
            nameValidator = TextValidator(nameEditText)
            nameEditText.addTextChangedListener(nameValidator)

            emailValidator = TextValidator(emailEditText)
            emailEditText.addTextChangedListener(emailValidator)

            passwordValidator = TextValidator(passwordEditText)
            passwordEditText.addTextChangedListener(passwordValidator)

        }

        // Inflate the user interface into the current activity.
        setContentView(binding.root)
    }

    /**
     * This method is called when the `Send` button is clicked.
     *
     * @param view The pressed view UI component.
     */
    fun onSendClick(view: View) {
        with (binding) {
            // Check the edit text validations.
            if (!nameValidator.isValidName) {
                nameEditText.error = getString(R.string.error)
                return
            }

            if (!emailValidator.isValidEmail) {
                emailEditText.error = getString(R.string.error)
                return
            }

            if (!passwordValidator.isValidPassword) {
                passwordEditText.error = getString(R.string.error)
                return
            }

            // Send the user data.
            toast("Data was successfully sent")
            onRevertClick(view)
        }
    }

    /**
     * This method is called when the `Revert` button is clicked.
     *
     * @param view The pressed view UI component.
     */
    fun onRevertClick(view: View) {
        // Clean the UI components data.
        with (binding) {
            nameEditText.setText("")
            nameEditText.isFocusable = false

            emailEditText.setText("")
            emailEditText.isFocusable = false

            passwordEditText.setText("")
            passwordEditText.isFocusable = false
        }

        // Hide the virtual keyboard.
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Make a standard toast that just contains text.
     *
     * @param text The text to show. Can be formatted text.
     * @param duration How long to display the message. Either `Toast.LENGTH_SHORT` or
     *      `Toast.LENGTH_LONG`.
     */
    private fun toast(text: CharSequence,
                      duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, text, duration).show()
    }

}
