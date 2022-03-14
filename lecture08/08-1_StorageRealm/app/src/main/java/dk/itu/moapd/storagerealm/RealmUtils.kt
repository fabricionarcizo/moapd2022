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
package dk.itu.moapd.storagerealm

import io.realm.Realm

/**
 * An util class to perform Realm tasks using asynchronous worker threads.
 */
class RealmUtils {

    /**
     * A set of static methods used in this class.
     */
    companion object {

        /**
         * This method adds a new item to the dataset and updates the RecyclerView.
         *
         * @param dummy The new item to be added to the dataset.
         */
        fun insertItem(dummy: Dummy) {
            // Sync all realm changes via a new instance.
            val realm = Realm.getDefaultInstance()

            // Execute Transaction asynchronously to avoid blocking the UI thread.
            realm.executeTransactionAsync {
                val id = it.where(Dummy::class.java).max("uid")
                dummy.uid = id?.toInt()?.plus(1) ?: 0
                it.insert(dummy)
            }

            // Always close realms when you are done with them.
            realm.close()
        }

        /**
         * This method update a selected item in the dataset and updates the RecyclerView.
         *
         * @param dummy The selected item to be updated in the dataset.
         * @param name String to be updated.
         */
        fun updateItem(dummy: Dummy, name: String) {
            // Get the item data.
            val uid = dummy.uid

            // Sync all realm changes via a new instance.
            val realm = Realm.getDefaultInstance()

            // Execute Transaction asynchronously to avoid blocking the UI thread.
            realm.executeTransactionAsync {
                val item = it.where(Dummy::class.java).equalTo("uid", uid).findFirst()
                item?.name = name
            }

            // Always close realms when you are done with them.
            realm.close()
        }

        /**
         * This method removes an item from the dataset and updates the RecyclerView.
         *
         * @param dummy The selected item to be updated in the dataset.
         */
        fun deleteItem(dummy: Dummy) {
            // Get the item to be deleted.
            val uid = dummy.uid

            // Sync all realm changes via a new instance.
            val realm = Realm.getDefaultInstance()

            // Execute Transaction asynchronously to avoid blocking the UI thread.
            realm.executeTransactionAsync {
                val item = it.where(Dummy::class.java).equalTo("uid", uid).findFirst()
                item?.deleteFromRealm()
            }

            // Always close realms when you are done with them.
            realm.close()
        }

    }

}
