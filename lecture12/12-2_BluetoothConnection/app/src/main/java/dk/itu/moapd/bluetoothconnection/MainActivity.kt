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
package dk.itu.moapd.bluetoothconnection

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.bluetoothconnection.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Bluetooth Connection application.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Create a BroadcastReceiver for ACTION_FOUND.
     */
    private val receiver = object : BroadcastReceiver() {

        /**
         * This method is called when the `BroadcastReceiver` is receiving an Intent broadcast.
         * During this time you can use the other methods on `BroadcastReceiver` to view/modify the
         * current result values. This method is always called within the main thread of its
         * process, unless you explicitly asked for it to be scheduled on a different thread using
         * `registerReceiver(BroadcastReceiver, IntentFilter, String, Handler)`. When it runs on the
         * main thread you should never perform long-running operations in it (there is a timeout of
         * 10 seconds that the system allows before considering the receiver to be blocked and a
         * candidate to be killed). You cannot launch a popup dialog in your implementation of
         * `onReceive()`.
         *
         * @param context The Context in which the receiver is running.
         * @param intent The Intent being received.
         */
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice object and its info
                    // from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    // Device parameters.
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address
                    if (deviceName != null)
                        adapter.addItem(
                            DummyModel(deviceName, deviceHardwareAddress, true)
                        )
                }
            }
        }
    }

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * An instance of the local device Bluetooth adapter.
     */
    private lateinit var bluetoothAdapter: BluetoothAdapter

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        private lateinit var adapter: CustomAdapter
        private const val REQUEST_CODE_PERMISSIONS = 10
        @RequiresApi(Build.VERSION_CODES.S)
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
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
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Request sensor permissions.
        if (!allPermissionsGranted())
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

        // Get the bluetooth adapter if it is available in the device.
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // Define the UI behavior.
        with (binding) {
            bluetoothButton.setOnClickListener {
                if (!bluetoothAdapter.isEnabled) {
                    startActivity(
                        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    )
                    bluetoothButton.text = getText(R.string.turn_off)
                } else {
                    bluetoothAdapter.disable()
                    bluetoothButton.text = getText(R.string.turn_on)
                }

                visibleButton.isEnabled = !bluetoothAdapter.isEnabled
            }

            visibleButton.setOnClickListener {
                startActivity(
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                )
            }
        }

        // Create the custom adapter to bind a list of strings.
        val data = ArrayList<DummyModel>()
        adapter = CustomAdapter(data)

        // Define the recycler view layout manager and adapter.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        binding.recyclerView.adapter = adapter

        // Show the pairedDevices in the RecyclerView.
        val pairedDevices = bluetoothAdapter.bondedDevices
        for (device in pairedDevices)
            adapter.addItem(
                DummyModel(device.name, device.address)
            )

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED)
        registerReceiver(receiver, filter)
        bluetoothAdapter.startDiscovery()

        // Inflate the user interface into the current activity.
        setContentView(binding.root)
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
        // Unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }

    /**
     * A method to show a dialog to the users as ask permission to access their Android mobile
     * device resources.
     *
     * @return `PackageManager#PERMISSION_GRANTED` if the given pid/uid is allowed that permission,
     *      or `PackageManager#PERMISSION_DENIED` if it is not.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}
