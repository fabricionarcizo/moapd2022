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

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import java.util.regex.Pattern

/**
 * A class to validate the content from `EditText` components.
 */
class TextValidator(private val view: View) : TextWatcher {

    /**
     * A boolean parameter to define the name validation status.
     */
    var isValidName = false
        private set

    /**
     * A boolean parameter to define the email validation status.
     */
    var isValidEmail = false
        private set

    /**
     * A boolean parameter to define the password validation status.
     */
    var isValidPassword = false
        private set

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        // This pattern doesn't work with Danish names. :)
        private val NAME_PATTERN = Pattern.compile(
            "^[A-Z]+[a-z]{2,}(?: [a-zA-Z]+)?(?: [a-zA-Z]+)?\$"
        )

        // The email regex pattern.
        private val EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        /**
         * This method validates the current data from the name `EditText` component.
         *
         * @param name The user's name.
         *
         * @return A boolean value with the validation result.
         */
        fun isValidName(name: CharSequence): Boolean {
            return NAME_PATTERN.matcher(name).matches()
        }

        /**
         * This method validates the current data from the email `EditText` component.
         *
         * @param email The user's email.
         *
         * @return A boolean value with the validation result.
         */
        fun isValidEmail(email: CharSequence): Boolean {
            return EMAIL_PATTERN.matcher(email).matches()
        }

        /**
         * This method validates the current data from the password `EditText` component.
         *
         * @param password The user's password.
         *
         * @return A boolean value with the validation result.
         */
        fun isValidPassword(password: CharSequence): Boolean {
            return password.length > 5
        }
    }

    /**
     * This method is called to notify you that, within <code>s</code>, the <code>count</code>
     * characters beginning at <code>start</code> are about to be replaced by new text with length
     * <code>after</code>. It is an error to attempt to make changes to <code>s</code> from this
     * callback.
     *
     * @param s The characters from the UI View component.
     * @param start The characters begins at the `start` index.
     * @param count The number of characters to be replace.
     * @param after The index where the new characters will be replaced.
     */
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    /**
     * This method is called to notify you that, within <code>s</code>, the <code>count</code>
     * characters beginning at <code>start</code> have just replaced old text that had length
     * <code>before</code>. It is an error to attempt to make changes to <code>s</code> from this
     * callback.
     *
     * @param s The characters from the UI View component.
     * @param start The characters begins at the `start` index.
     * @param before The index where the new characters will be replaced.
     * @param count The number of characters to be replace.
     */
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    /**
     * This method is called to notify you that, somewhere within <code>s</code>, the text has been
     * changed. It is legitimate to make further changes to <code>s</code> from this callback, but
     * be careful not to get yourself into an infinite loop, because any changes you make will cause
     * this method to be called again recursively.
     *
     * (You are not told where the change took place because other `afterTextChanged()` methods may
     * already have made other changes and invalidated the offsets. But if you need to know here,
     * you can use `Spannable#setSpan()` in `onTextChanged()` to mark your place and then look up
     * from here where the span ended up.
     *
     * @param s The UI View component.
     */
    override fun afterTextChanged(s: Editable?) {
        when (view.id) {
            R.id.name_edit_text ->
                isValidName = isValidName(s.toString())
            R.id.email_edit_text ->
                isValidEmail = isValidEmail(s.toString())
            R.id.password_edit_text ->
                isValidPassword = isValidPassword(s.toString())
        }
    }

}
