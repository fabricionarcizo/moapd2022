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
import androidx.lifecycle.LiveData

/**
 * A class that represents a single source of truth for all app data, clean API for UI to
 * communicate with.
 */
class DummyRepository(application: Application) {

    /**
     * The `Data Access Object (DAO)` used to access the `Dummy` table.
     */
    private val dummyDao: DummyDao

    /**
     * The `LiveData` used to monitor the elements available in the `Dummy` table.
     */
    private val data: LiveData<List<Dummy>>

    // Initializes the class attributes.
    init {
        val db = AppDatabase.getDatabase(application)
        dummyDao = db.dummyDao()
        data = dummyDao.getAll()
    }

    /**
     * This method inserts a new `Dummy` instance in the dataset.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    suspend fun insert(dummy: Dummy) {
        dummyDao.insert(dummy)
    }

    /**
     * This method updates the parameters of a specific `Dummy` instance in the dataset.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    suspend fun update(dummy: Dummy) {
        dummyDao.update(dummy)
    }

    /**
     * This method deletes a specific `Dummy` instance from the dataset.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    suspend fun delete(dummy: Dummy) {
        dummyDao.delete(dummy)
    }

    /**
     * This method selects all columns of all elements from the `dummy` table.
     *
     * @return A list of all elements of `dummy` table.
     */
    fun getAll() = data

    /**
     * This method selects a specific element from the `dummy` table based on the parameter `name`.
     *
     * @param name The name of `dummy` object.
     *
     * @return An instance of `Dummy` class.
     */
    fun findByName(name: String) = dummyDao.findByName(name)

}
