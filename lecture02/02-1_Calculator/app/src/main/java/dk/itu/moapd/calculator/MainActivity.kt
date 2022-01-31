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
package dk.itu.moapd.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import dk.itu.moapd.calculator.databinding.ActivityMainBinding
import dk.itu.moapd.calculator.databinding.ButtonsLayoutBinding
import dk.itu.moapd.calculator.databinding.ResultLayoutBinding
import org.mozilla.javascript.Context

/**
 * An activity class with methods to manage the main activity of Calculator application.
 */
class MainActivity : AppCompatActivity() {

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var buttonsBinding: ButtonsLayoutBinding
    private lateinit var resultBinding: ResultLayoutBinding

    /**
     * A `String` to persist the latest mathematical symbol pressed by the user.
     */
    private lateinit var symbol: String

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
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        buttonsBinding = ButtonsLayoutBinding.bind(mainBinding.root)
        resultBinding = ResultLayoutBinding.bind(mainBinding.root)

        // The initial symbol state.
        symbol = ""

        // Define the UI buttons behavior.
        with (buttonsBinding) {

            // The button zero listener.
            buttonZero.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_zero)
            }

            // The button double zero listener.
            buttonDoubleZero.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_double_zero)
            }

            // The button one listener.
            buttonOne.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_one)
            }

            // The button two listener.
            buttonTwo.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_two)
            }

            // The button three listener.
            buttonThree.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_three)
            }

            // The button four listener.
            buttonFour.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_four)
            }

            // The button five listener.
            buttonFive.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_five)
            }

            // The button six listener.
            buttonSix.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_six)
            }

            // The button seven listener.
            buttonSeven.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_seven)
            }

            // The button eight listener.
            buttonEight.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_eight)
            }

            // The button nine listener.
            buttonNine.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentValue(R.string.button_nine)
            }

            // The button clear listener.
            buttonClear.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                resultBinding.currentNumber.text = getString(R.string.button_zero)
                resultBinding.equation.text = ""
                symbol = ""
            }

            // The button percentage listener.
            buttonPercentage.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentEquation("%")
            }

            // The button divide listener.
            buttonDivide.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentEquation("/")
            }

            // The button multiple listener.
            buttonMultiple.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentEquation("X")
            }

            // The button minus listener.
            buttonMinus.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentEquation("-")
            }

            // The button add listener.
            buttonAdd.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setCurrentEquation("+")
            }

            // The button dot listener.
            buttonDot.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                var value = resultBinding.currentNumber.text as String
                if (!value.contains("."))
                    value += "."
                if (value == ".")
                    value = "0."
                resultBinding.currentNumber.text = value
            }

            // The button equal listener.
            buttonEqual.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                calculateEquation()
            }
        }

        // Inflate the user interface into the current activity.
        setContentView(mainBinding.root)
    }

    /**
     * Evaluates the last digit pressed and updates the current number field.
     *
     * @param number The latest number pressed by the user in the calculator
     */
    private fun setCurrentValue(number: Int) {

        // Define the UI result components behavior.
        with (resultBinding) {

            // Check if the user has pressed any mathematical symbol.
            if (symbol.isEmpty())
                equation.text = ""

            // Get the current number as a `String`.
            var currentValue = currentNumber.text as String
            if (currentValue.isEmpty())
                currentValue = "0"

            // Maximum number of digits allowed.
            if (currentValue.length > 11)
                return

            // Returns a localized string from the application's package's default string table.
            val newValue = getString(number)
            if (currentValue.toFloat() == 0f &&
                !currentValue.contains(".")
            ) {
                if (newValue.toFloat() == 0f)
                    return
                currentValue = ""
            }

            // Updates the current value.
            currentValue += newValue
            currentNumber.text = currentValue
        }
    }

    /**
     * Evaluates the last digit pressed and updates the current equation field.
     *
     * @param _symbol A mathematical symbol pressed byt the user in the calculator.
     */
    private fun setCurrentEquation(_symbol: String) {

        // Define the UI result components behavior.
        with (resultBinding) {

            // Check if the user only has pressed a mathematical symbol.
            if (currentNumber.text.isEmpty()) {
                symbol = _symbol
                return
            }

            if (symbol == "%" && _symbol == "%")
                return

            // Updates the current equation.
            val currentValue = currentNumber.text as String
            var currentEquation = equation.text as String

            currentEquation += symbol + currentValue
            if (_symbol == "%")
                currentEquation += "%"

            equation.text = currentEquation
            symbol = _symbol

            currentNumber.text = ""
        }
    }

    /**
     * Uses an external package (i.e., Rhino) to calculate the current mathematical equation.
     */
    private fun calculateEquation() {

        // Define the UI result components behavior.
        with (resultBinding) {

            // Check if there is a valid input data.
            if (symbol.isEmpty() || equation.text == "Error")
                return

            setCurrentEquation("")

            // Uses Rhino to calculate a mathematical expression using JavaScript.
            // See: https://github.com/mozilla/rhino
            var result: String
            var eq = equation.text as String
            eq = eq.replace("X", "*")
            eq = eq.replace("%", "/100")

            try {
                val rhino = Context.enter().apply {
                    optimizationLevel = -1
                }
                val scope = rhino.initStandardObjects()
                result = rhino.evaluateString(
                    scope, eq, "JavaScript", 1, null
                ).toString()
            } catch (ex: Exception) {
                result = "Error"
            }

            // Updates the current equation result.
            equation.text = result
            currentNumber.text = ""
        }
    }

}
