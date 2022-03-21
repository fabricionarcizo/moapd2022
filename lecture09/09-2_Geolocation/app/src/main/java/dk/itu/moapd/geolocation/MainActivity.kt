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
package dk.itu.moapd.geolocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import dk.itu.moapd.geolocation.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

/**
 * An activity class with methods to manage the main activity of Geolocation application.
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
     * The primary instance for receiving location updates.
     */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * This callback is called when `FusedLocationProviderClient` has a new `Location`.
     */
    private lateinit var locationCallback: LocationCallback

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011
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
            viewModel.addFragment(LocationFragment())
            viewModel.addFragment(MapsFragment())
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

            // Bottom navigation view actions.
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    // Handle tab select
                    viewModel.setFragment(tab?.position!!)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Handle tab reselect
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Handle tab unselect
                }
            })
        }

        // Start the location-aware method.
        startLocationAware()

        // Inflate the user interface into the current activity.
        setContentView(binding.root)
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
        subscribeToLocationUpdates()
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
        unsubscribeToLocationUpdates()
    }

    /**
     * Start the location-aware instance and defines the callback to be called when the GPS sensor
     * provides a new user's location.
     */
    private fun startLocationAware() {

        // Show a dialog to ask the user to allow the application to access the device's location.
        requestUserPermissions()

        // Start receiving location updates.
        fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(this)

        // Initialize the `LocationCallback`.
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                viewModel.onLocationChanged(locationResult.lastLocation)
            }
        }
    }

    /**
     * Create a set of dialogs to show to the users and ask them for permissions to get the device's
     * resources.
     */
    private fun requestUserPermissions() {
        // An array with location-aware permissions.
        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        // Check which permissions is needed to ask to the user.
        val permissionsToRequest = permissionsToRequest(permissions)

        // Show the permissions dialogs to the user.
        if (permissionsToRequest.size > 0)
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                ALL_PERMISSIONS_RESULT
            )
    }

    /**
     * Create an array with the permissions to show to the user.
     *
     * @param permissions An array with the permissions needed by this applications.
     *
     * @return An array with the permissions needed to ask to the user.
     */
    private fun permissionsToRequest(permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions)
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                result.add(permission)
        return result
    }

    /**
     * This method checks if the user allows the application uses all location-aware resources to
     * monitor the user's location.
     *
     * @return A boolean value with the user permission agreement.
     */
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

    /**
     * Subscribes this application to get the location changes via the `locationCallback()`.
     */
    @SuppressLint("MissingPermission")
    private fun subscribeToLocationUpdates() {
        // Check if the user allows the application to access the location-aware resources.
        if (checkPermission())
            return

        // Sets the accuracy and desired interval for active location updates.
        val locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(5)
            fastestInterval = TimeUnit.SECONDS.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Subscribe to location changes.
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    /**
     * Unsubscribes this application of getting the location changes from  the `locationCallback()`.
     */
    private fun unsubscribeToLocationUpdates() {
        // Unsubscribe to location changes.
        fusedLocationProviderClient
            .removeLocationUpdates(locationCallback)
    }

}
