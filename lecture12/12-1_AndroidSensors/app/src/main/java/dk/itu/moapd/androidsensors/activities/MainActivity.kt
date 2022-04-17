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
package dk.itu.moapd.androidsensors.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.androidsensors.R
import dk.itu.moapd.androidsensors.databinding.ActivityMainBinding
import dk.itu.moapd.androidsensors.fragments.environmental.EnvironmentalFragment
import dk.itu.moapd.androidsensors.fragments.motion.MotionFragment
import dk.itu.moapd.androidsensors.fragments.position.PositionFragment
import dk.itu.moapd.androidsensors.viewmodels.MainActivityVM

/**
 * An activity class with methods to manage the main activity of Android Sensors application.
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
     * Using lazy initialization to create the view model instance when the user access the object
     * for the first time.
     */
    private val viewModel: MainActivityVM by lazy {
        ViewModelProvider(this)
            .get(MainActivityVM::class.java)
    }

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        @RequiresApi(Build.VERSION_CODES.Q)
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
        )
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
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Request sensor permissions.
        if (!allPermissionsGranted())
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

        // Get the latest fragment added in the fragment manager.
        val lastFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)

        // Create the fragments instances.
        if (lastFragment == null) {
            viewModel.addFragment(MotionFragment())
            viewModel.addFragment(EnvironmentalFragment())
            viewModel.addFragment(PositionFragment())
            viewModel.setFragment(0)
        }

        // Add the fragment into the activity.
        for (fragment in viewModel.getFragmentList())
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_view, fragment)
                .hide(fragment)
                .commit()

        // The current activity.
        var activeFragment: Fragment = viewModel.fragmentState.value!!

        // Execute this when the user sets a specific fragment.
        viewModel.fragmentState.observe(this, { fragment ->
            supportFragmentManager
                .beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
            activeFragment = fragment
        })

        // Define the UI components behavior.
        with (binding) {

            bottomNavigation.selectedItemId = viewModel.getButtonId()

            // Bottom navigation view actions.
            bottomNavigation.setOnItemSelectedListener { item ->
                when(item.itemId) {

                    // Select the Page Fragment bottom.
                    R.id.page_motion -> {
                        viewModel.setFragment(0)
                        viewModel.setButtonId(R.id.page_motion)
                        true
                    }

                    // Select the Environmental Fragment bottom.
                    R.id.page_environmental -> {
                        viewModel.setFragment(1)
                        viewModel.setButtonId(R.id.page_environmental)
                        true
                    }

                    // Select the Position Fragment bottom.
                    R.id.page_position -> {
                        viewModel.setFragment(2)
                        viewModel.setButtonId(R.id.page_position)
                        true
                    }

                    else -> false
                }
            }
        }

        // Inflate the user interface into the current activity.
        setContentView(binding.root)
    }

    /**
     * A method to show a dialog to the users as ask permission to access their Android mobile
     * device resources.
     *
     * @return `PackageManager#PERMISSION_GRANTED` if the given pid/uid is allowed that permission,
     *      or `PackageManager#PERMISSION_DENIED` if it is not.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}
