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

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * An interface to represent how to access the dummy object in the database. This `Data Access
 * Object` (DAO) specifies the SQL queries and associates them with methods of an `Entity` class.
 */
@Dao
interface DummyDao {

    /**
     * Implement this method to insert a new `dummy` element in the database.
     *
     * @param dummy An instance of `Dummy` class.
     */
    @Insert
    suspend fun insert(dummy: Dummy)

    /**
     * Implement this method to update the parameters of a specific `dummy` instance in the
     * database.
     *
     * @param dummy An instance of `Dummy` class.
     */
    @Update
    suspend fun update(dummy: Dummy)

    /**
     * Implement this method to delete a `dummy` element from the database.
     *
     * @param dummy An instance of `Dummy` class.
     */
    @Delete
    suspend fun delete(dummy: Dummy)

    /**
     * Implement this method to select all columns of all elements from the `dummy` table.
     *
     * @return A list of all elements of `dummy` table.
     */
    @Query("SELECT * FROM dummy")
    fun getAll(): LiveData<List<Dummy>>

    /**
     * Implement this method to select a specific element from the `dummy` table based on the
     * parameter `name`.
     *
     * @param name The name of `dummy` object.
     *
     * @return An instance of `Dummy` class.
     */
    @Query("SELECT * FROM dummy WHERE name LIKE :name")
    fun findByName(name: String): LiveData<Dummy?>

}
