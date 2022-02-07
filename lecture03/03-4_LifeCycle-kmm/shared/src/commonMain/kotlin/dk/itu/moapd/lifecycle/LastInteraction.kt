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

/**
 * A shared class to be executed in multi-platforms, i.e. Android and iOS.
 */
class LastInteraction {

    /**
     * Set a number `id` and an optional boolean flag, and return a `String` that corresponds to the
     * latest UI component used by the user.
     *
     * @param num The UI component `id`.
     * @param status The checkbox boolean.
     *
     * @return A string that corresponds to the latest UI component used by the user.
     */
    fun interactionText(num: Int, status: Boolean = false) = when(num) {
        0 -> "You clicked on the true button"
        1 -> "You clicked on the false button"
        2 -> when(status) {
            true -> "You checked the checkbox"
            false -> "You unchecked the checkbox"
        }
        else -> "Hello World!"
    }

}
