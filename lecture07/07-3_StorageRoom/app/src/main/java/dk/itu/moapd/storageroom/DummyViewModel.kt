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
package dk.itu.moapd.storageroom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * A view model sensitive to changes in the `Dummy` dataset.
 */
class DummyViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * A repository to access the database methods of `Dummy` table.
     */
    private val dummyRepository = DummyRepository(application)

    /**
     * The `LiveData` used to monitor the elements available in the `Dummy` table.
     */
    private val data: LiveData<List<Dummy>> = dummyRepository.getAll()

    /**
     * This method inserts a new `Dummy` instance in the dataset. It uses Kotlin Coroutine KTX to
     * execute the database query in the `ViewModel` scope.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    fun insert(dummy: Dummy) = viewModelScope.launch {
        dummyRepository.insert(dummy)
    }

    /**
     * This method updates the parameters of a specific `Dummy` instance in the dataset. It uses
     * Kotlin Coroutine KTX to execute the database query in the `ViewModel` scope.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    fun update(dummy: Dummy) = viewModelScope.launch {
        dummyRepository.update(dummy)
    }

    /**
     * This method deletes a specific `Dummy` instance from the dataset. It uses Kotlin Coroutine
     * KTX to execute the database query in the `ViewModel` scope.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    fun delete(dummy: Dummy) = viewModelScope.launch {
        dummyRepository.delete(dummy)
    }

    /**
     * This method selects all columns of all elements from the `dummy` table.
     *
     * @return A list of all elements of `dummy` table.
     */
    fun getAll(): LiveData<List<Dummy>> = data

    /**
     * This method selects a specific element from the `dummy` table based on the parameter `name`.
     *
     * @param name The name of `dummy` object.
     *
     * @return An instance of `Dummy` class.
     */
    fun findByName(name: String) = dummyRepository.findByName(name)

}
