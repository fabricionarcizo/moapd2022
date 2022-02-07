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
        private const val TEXT = "text"
        private const val IS_CHECKED = "isChecked"
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
                textView.text = getString(R.string.true_text)
            }

            falseButton.setOnClickListener{
                textView.text = getString(R.string.false_text)
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val status = if (isChecked) "checked" else "unchecked"
                textView.text = resources.getString(R.string.selected_text, status)
            }

            // Retrieve per-instance state.
            if (savedInstanceState != null) {
                checkBox.isChecked = savedInstanceState.getBoolean(IS_CHECKED, false)
                textView.text = savedInstanceState.getString(TEXT, "Hello World!")
            }
        }

        // Inflate the user interface into the current activity.
        setContentView(binding.root)

        Log.i(TAG, "onCreate() called")
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state
     * can be restored in `onCreate()` or `onRestoreInstanceState()` (the `Bundle` populated by this
     * method will be passed to both).
     *
     * This method is called before an activity may be killed so that when it comes back some time
     * in the future it can restore its state. For example, if activity `B` is launched in front of
     * activity `A`, and at some point activity `A` is killed to reclaim resources, activity `A`
     * will have a chance to save the current state of its user interface via this method so that
     * when the user returns to activity `A`, the state of the user interface can be restored via
     * `onCreate()` or `onRestoreInstanceState()`.
     *
     * Do not confuse this method with activity lifecycle callbacks such as `onPause()`, which is
     * always called when the user no longer actively interacts with an activity, or `onStop()`
     * which is called when activity becomes invisible. One example of when `onPause()` and
     * `onStop()` is called and not this method is when a user navigates back from activity `B` to
     * activity `A`: there is no need to call `onSaveInstanceState()` on `B` because that particular
     * instance will never be restored, so the system avoids calling it. An example when `onPause()`
     * is called and not `onSaveInstanceState()` is when activity `B` is launched in front of
     * activity `A`: the system may avoid calling `onSaveInstanceState()` on activity `A` if it
     * isn't killed during the lifetime of `B` since the state of the user interface of `A` will
     * stay intact.
     *
     * The default implementation takes care of most of the UI per-instance state for you by calling
     * `android.view.View#onSaveInstanceState()` on each view in the hierarchy that has an id, and
     * by saving the id of the currently focused view (all of which is restored by the default
     * implementation of `onRestoreInstanceState()`. If you override this method to save additional
     * information not captured by each individual view, you will likely want to call through to the
     * default implementation, otherwise be prepared to save all of the state of each view yourself.
     *
     * If called, this method will occur after `onStop()` for applications targeting platforms
     * starting with `android.os.Build.VERSION_CODES#P`. For applications targeting earlier platform
     * versions this method will occur before `onStop()` and there are no guarantees about whether
     * it will occur before or after `onPause()`.
     */
    override fun onSaveInstanceState(outState: Bundle) {

        with (binding) {
            val text = textView.text.toString()
            outState.putString(TEXT, text)

            val isChecked = checkBox.isChecked
            outState.putBoolean(IS_CHECKED, isChecked)
        }

        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState() called")
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
