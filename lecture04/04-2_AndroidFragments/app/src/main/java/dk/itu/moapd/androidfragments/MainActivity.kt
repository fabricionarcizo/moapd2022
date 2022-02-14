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
package dk.itu.moapd.androidfragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.androidfragments.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Android Fragments application.
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
    private val viewModel: MainActivityVM by lazy {
        ViewModelProvider(this)
            .get(MainActivityVM::class.java)
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

        // Get the latest fragment added in the fragment manager.
        val lastFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)

        // Create the fragments instances.
        if (lastFragment == null) {
            viewModel.addFragment(MainFragment())
            viewModel.addFragment(SecondFragment())
            viewModel.addFragment(ThirdFragment())
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
        viewModel.fragmentState.observe(this) { fragment ->
            supportFragmentManager
                .beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
            activeFragment = fragment
        }

        // Define the UI components behavior.
        with (binding) {

            bottomNavigation?.selectedItemId = viewModel.getButtonId()
            navigationRail?.selectedItemId = viewModel.getButtonId()

            // Bottom navigation view actions.
            bottomNavigation?.setOnItemSelectedListener { item ->
                when(item.itemId) {

                    // Select the Fragment 1 bottom.
                    R.id.page_1 -> {
                        viewModel.setFragment(0)
                        viewModel.setButtonId(R.id.page_1)
                        true
                    }

                    // Select the Fragment 2 bottom.
                    R.id.page_2 -> {
                        viewModel.setFragment(1)
                        viewModel.setButtonId(R.id.page_2)
                        true
                    }

                    // Select the Fragment 3 bottom.
                    R.id.page_3 -> {
                        viewModel.setFragment(2)
                        viewModel.setButtonId(R.id.page_3)
                        true
                    }

                    else -> false
                }
            }

            // Navigation rail view actions.
            navigationRail?.setOnItemSelectedListener { item ->
                when(item.itemId) {

                    // Select the Fragment 1 bottom.
                    R.id.page_1 -> {
                        viewModel.setFragment(0)
                        viewModel.setButtonId(R.id.page_1)
                        true
                    }

                    // Select the Fragment 2 bottom.
                    R.id.page_2 -> {
                        viewModel.setFragment(1)
                        viewModel.setButtonId(R.id.page_2)
                        true
                    }

                    // Select the Fragment 3 bottom.
                    R.id.page_3 -> {
                        viewModel.setFragment(2)
                        viewModel.setButtonId(R.id.page_3)
                        true
                    }

                    else -> false
                }
            }
        }

        // Inflate the user interface into the current activity.
        setContentView(binding.root)

        Log.i(TAG, "onCreate() called")
    }

}
