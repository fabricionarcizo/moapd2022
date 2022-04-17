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
package dk.itu.moapd.androidsensors.fragments.motion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.moapd.androidsensors.R
import dk.itu.moapd.androidsensors.databinding.FragmentThreeAxesBinding
import java.lang.Float.max
import java.lang.Float.min

/**
 * A fragment to show the gyroscope data get from an Android position sensor.
 *
 * The `MainActivity` has a `FragmentContainerView` area to replace dynamically the fragments used
 * by this project. You can use a bundle to share data between the main activity and this fragment.
 * We have used a `ViewModel` from the `MainActivity` to execute the updates on UI components.
 */
class GyroscopeFragment : Fragment() {

    /**
     * Used for receiving notifications from the SensorManager when there is new sensor data.
     */
    private val gyroscopeListener: SensorEventListener = object : SensorEventListener {

        /**
         * Called when there is a new sensor event. Note that "on changed" is somewhat of a
         * misnomer, as this will also be called if we have a new reading from a sensor with the
         * exact same sensor values (but a newer timestamp).
         *
         * The application doesn't own the `android.hardware.SensorEvent` object passed as a
         * parameter and therefore cannot hold on to it. The object may be part of an internal pool
         * and may be reused by the framework.
         *
         * @param event The SensorEvent instance.
         */
        override fun onSensorChanged(event: SensorEvent) {
            with (binding) {
                circularProgressIndicatorX.progress = event.values[0].normalize()
                circularProgressIndicatorY.progress = event.values[1].normalize()
                circularProgressIndicatorZ.progress = event.values[2].normalize()

                axisXValue.text = getString(R.string.degrees_text, event.values[0].toDegrees())
                axisYValue.text = getString(R.string.degrees_text, event.values[1].toDegrees())
                axisZValue.text = getString(R.string.degrees_text, event.values[2].toDegrees())
            }
        }

        /**
         * Called when the accuracy of the registered sensor has changed. Unlike
         * `onSensorChanged()`, this is only called when this accuracy value changes.
         *
         * @param sensor An instance of the `Sensor` class.
         * @param accuracy The new accuracy of this sensor, one of `SensorManager.SENSOR_STATUS_`
         */
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }

    }

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private var _binding: FragmentThreeAxesBinding? = null

    /**
     * This property is only valid between `onCreateView()` and `onDestroyView()` methods.
     */
    private val binding get() = _binding!!

    /**
     * An instance that lets you access the Android device's sensors.
     */
    private lateinit var sensorManager: SensorManager

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between `onCreate(Bundle)` and
     * `onViewCreated(View, Bundle)`. A default View can be returned by calling `Fragment(int)` in
     * your constructor. Otherwise, this method returns `null`.
     *
     * It is recommended to <strong>only</strong> inflate the layout in this method and move logic
     * that operates on the returned View to `onViewCreated(View, Bundle)`.
     *
     * If you return a View from here, you will later be called in `onDestroyView()` when the view
     * is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the
     *      fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be
     *      attached to. The fragment should not add the view itself, but this can be used to
     *      generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment.
        _binding = FragmentThreeAxesBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after `onCreateView(LayoutInflater, ViewGroup, Bundle)` has returned, but
     * before any saved state has been restored in to the view. This gives subclasses a chance to
     * initialize themselves once they know their view hierarchy has been completely created. The
     * fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get an instance of the Android Sensor Manager.
        val service = Context.SENSOR_SERVICE
        sensorManager = requireActivity().getSystemService(service) as SensorManager
    }

    /**
     * Called when the fragment is visible to the user and actively running. This is generally tied
     * to `Activity.onResume()` of the containing Activity's lifecycle.
     */
    override fun onResume() {
        super.onResume()

        // Get an instance of the gyroscope sensor and register the sensor listener.
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (gyroscope != null)
            sensorManager.registerListener(gyroscopeListener,
                gyroscope, SensorManager.SENSOR_DELAY_NORMAL)

        // Otherwise, inform to the users that there is no gyroscope sensor in their mobile
        // devices.
        else {
            with (binding) {
                axisXValue.text = getString(R.string.unavailable)
                axisYValue.text = getString(R.string.unavailable)
                axisZValue.text = getString(R.string.unavailable)
            }
        }

        // Set the axes labels.
        with (binding) {
            axisX.text = getString(R.string.rotation_x)
            axisY.text = getString(R.string.rotation_y)
            axisZ.text = getString(R.string.rotation_z)
        }
    }

    /**
     * Called when the Fragment is no longer resumed. This is generally tied to `Activity.onPause()`
     * of the containing Activity's lifecycle.
     */
    override fun onPause() {
        super.onPause()

        // When the Fragment is not visible, unregister the gyroscope listener.
        sensorManager.unregisterListener(gyroscopeListener)
    }

    /**
     * Called when the view previously created by `onCreateView()` has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created. This
     * is called after `onStop()` and before `onDestroy()`. It is called <em>regardless</em> of
     * whether `onCreateView()` returned a non-null view. Internally it is called after the view's
     * state has been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Normalizes the sensor data into the circular progress indicator range.
     *
     * @return An integer normalized value between 0 and 100.
     */
    private fun Float.normalize(): Int {
        val norm = min(max(this, -SensorManager.STANDARD_GRAVITY), SensorManager.STANDARD_GRAVITY)
        return ((norm + SensorManager.STANDARD_GRAVITY) /
                (2f * SensorManager.STANDARD_GRAVITY) * 100).toInt()
    }

    /**
     * Convert the current radians value to degrees.
     *
     * @return An integer degree value between -360 and +360.
     */
    private fun Float.toDegrees(): Int = (this * 180 / Math.PI).toInt()

}
