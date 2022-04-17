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
package dk.itu.moapd.androidbroadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

/**
 * A broadcast receiver class with a method to play an alarm in background.
 */
class AlarmReceiver : BroadcastReceiver() {

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
        // Show a pop-up message to the user.
        Toast.makeText(context, "Timer done!", Toast.LENGTH_LONG).show()

        // Get the vibrator instance.
        val vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Vibrate the user's device for 2 seconds.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    2000, VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        else
            vibrator.vibrate(2000)
    }

}
