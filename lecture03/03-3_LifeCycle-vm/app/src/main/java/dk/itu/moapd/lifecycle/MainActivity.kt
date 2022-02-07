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
package dk.itu.moapd.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.lifecycle.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Life Cycle application.
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
     * A set of private constants used in this class.
     */
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    /**
     * Using lazy initialization to create the view model instance when the user access the object
     * for the first time.
     */
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)
            .get(MainViewModel::class.java)
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

        // Define the UI components behavior.
        with (binding) {

            trueButton.setOnClickListener{
                viewModel.onTextChanged(getString(R.string.true_text))
            }

            falseButton.setOnClickListener{
                viewModel.onTextChanged(getString(R.string.false_text))
            }

            checkBox.setOnClickListener {
                viewModel.toggleChecked()
                val status = if (viewModel.isChecked()) "checked" else "unchecked"
                viewModel.onTextChanged(resources.getString(R.string.selected_text, status))
            }
        }

        // Execute this when the user interacts with any UI component.
        viewModel.textState.observe(this, { text ->
            binding.textView.text = text
        })

        // Inflate the user interface into the current activity.
        setContentView(binding.root)

        Log.i(TAG, "onCreate() called")
    }

    /**
     * Called after `onCreate()` or after `onRestart()` when the activity had been stopped, but is
     * now again being displayed to the user. It will usually be followed by `onResume()`. This is a
     * good place to begin drawing visual elements, running animations, etc.
     *
     * You can call `finish()` from within this function, in which case `onStop()` will be
     * immediately called after `onStart()` without the lifecycle transitions in-between
     * (`onResume()`, `onPause()`, etc) executing.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart() called")
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
        Log.i(TAG, "onResume() called")
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
        Log.i(TAG, "onPause() called")
    }

    /**
     * Called when you are no longer visible to the user. You will next receive either
     * `onRestart()`, `onDestroy()`, or nothing, depending on later user activity. This is a good
     * place to stop refreshing UI, running animations and other visual things.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop() called")
    }

    /**
     * Called after `onStop()` when the current activity is being re-displayed to the user (the user
     * has navigated back to it). It will be followed by `onStart()` and then `onResume()`.
     *
     * For activities that are using raw `Cursor` objects (instead of creating them through
     * `managedQuery(android.net.Uri, String[], String, String[], String)`, this is usually the
     * place where the cursor should be required (because you had deactivated it in `onStop()`.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart() called")
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
        Log.i(TAG, "onDestroy() called")
    }

}
