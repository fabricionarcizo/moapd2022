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
package dk.itu.moapd.androidsensors.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A view model sensitive to changes in the `MainActivity` fragments.
 */
class MainActivityVM : ViewModel() {

    /**
     * A list of all available fragments used by the main activity. P.S.: These instances are
     * created only once, when the app executes the `onCreate()` method for the first time.s
     */
    private val fragments = ArrayList<Fragment>()

    /**
     * This method will be executed when the main activity adds a new fragment into the user
     * interface.
     *
     * @param fragment A new fragment to show in the user interface.
     */
    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    /**
     * This method returns a list of all instances of fragments used by the main activity.
     *
     * @return A list of all available fragments.
     */
    fun getFragmentList() : List<Fragment> = fragments

    /**
     * The current selected fragment to show in the user interface.
     */
    private val fragment = MutableLiveData<Fragment>()

    /**
     * A `LiveData` which publicly exposes any update in the current shown fragment.
     */
    val fragmentState: LiveData<Fragment>
        get() = fragment

    /**
     * This method will be executed when the user selects a new fragment to show in the main
     * activity. It sets the text into the LiveData instance.
     *
     * @param index An ID of the selected fragment.
     */
    fun setFragment(index: Int) {
        fragment.value = fragments.elementAt(index)
    }

    /**
     * The ID of the latest button pressed by the user.
     */
    private var buttonId = 0

    /**
     * This method returns the ID resource of the latest button pressed by the user in the main UI.
     *
     * @return The ID resource of a Material Design button.
     */
    fun getButtonId() = buttonId

    /**
     * This method will be executed when the user press a new button in the user interface.
     *
     * @param buttonId The ID resource of a Material Design button.
     */
    fun setButtonId(buttonId: Int) {
        this.buttonId = buttonId
    }

}
