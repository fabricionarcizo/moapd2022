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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dk.itu.moapd.storagerealm.databinding.ActivityMainBinding
import io.realm.Realm

/**
 * An activity class with methods to manage the main activity of Storage Realm application.
 */
class MainActivity : AppCompatActivity(), ItemClickListener {

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * An extension of `AlertDialog.Builder` to create custom dialogs using a Material theme (e.g.,
     * Theme.MaterialComponents).
     */
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder

    /**
     * Inflates a custom Android layout used in the input dialog.
     */
    private lateinit var customAlertDialogView: View

    /**
     * A Realm static instance defined by the `io.realm.RealmConfiguration` set by
     * `setDefaultConfiguration(RealmConfiguration)`.
     */
    private lateinit var realm: Realm

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        private lateinit var adapter: CustomAdapter
    }

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

        // Get the Realm static instance.
        realm = Realm.getDefaultInstance()

        // Create the custom adapter to bind a list of Strings.
        adapter = CustomAdapter(this,
            realm.where(Dummy::class.java).sort("uid").findAll())

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Create a MaterialAlertDialogBuilder instance.
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)

        // Define the add button behavior.
        binding.floatingActionButton.setOnClickListener {

            // Inflate Custom alert dialog view
            customAlertDialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_data, binding.root, false)

            // Launching the custom alert dialog
            launchInsertAlertDialog()
        }

        // Define the recycler view layout manager.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        binding.recyclerView.adapter = adapter

        // Adding the swipe option.
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                super.onSwiped(viewHolder, direction)
                val adapter = binding.recyclerView.adapter as CustomAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        // Inflate the user interface into the current activity.
        val view = binding.root
        setContentView(view)
    }

    /**
     * Perform any final cleanup before an activity is destroyed. This can happen either because the
     * activity is finishing (someone called `finish()` on it), or because the system is temporarily
     * destroying this instance of the activity to save space. You can distinguish between these two
     * scenarios with the `isFinishing()` method.
     *
     * Note: do not count on this method being called as a place for saving data! For example, if an
     * activity is editing data in a content provider, those edits should be committed in either
     * `onPause()` or `onSaveInstanceState()`, not here. This method is usually implemented to free
     * resources like threads that are associated with an activity, so that a destroyed activity
     * does not leave such things around while the rest of its application is still running. There
     * are situations where the system will simply kill the activity's hosting process without
     * calling this method (or any others) in it, so it should not be used to do things that are
     * intended to remain around after the process goes away.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /**
     * This method will be executed when the user press an item in the `RecyclerView` for a long
     * time.
     *
     * @param dummy An instance of `Dummy` class.
     */
    override fun onItemClickListener(dummy: Dummy?) {
        // Inflate Custom alert dialog view
        customAlertDialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_data, binding.root, false)

        // Launching the custom alert dialog
        launchUpdateAlertDialog(dummy!!)
    }

    /**
     * Building the insert alert dialog using the `MaterialAlertDialogBuilder` instance. This method
     * shows a dialog with a single edit text. The user can type a name and add it to the text file
     * dataset or cancel the operation.
     */
    private fun launchInsertAlertDialog() {
        val textField = customAlertDialogView.findViewById<TextInputLayout>(R.id.name_text_field)

        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(getString(R.string.dialog_add_title))
            .setMessage(getString(R.string.dialog_add_message))
            .setPositiveButton(getString(R.string.add_button)) { dialog, _ ->
                val name = textField.editText?.text.toString()
                if (name.isNotEmpty())
                    RealmUtils.insertItem(Dummy(0, name))

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Building the update alert dialog using the `MaterialAlertDialogBuilder` instance. This method
     * shows a dialog with a single edit text. The user can type a name and add it to the text file
     * dataset or cancel the operation.
     *
     * @param dummy An instance of `Dummy` class.
     */
    private fun launchUpdateAlertDialog(dummy: Dummy) {
        val textField = customAlertDialogView.findViewById<TextInputLayout>(R.id.name_text_field)
        textField.editText?.setText(dummy.name)

        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(getString(R.string.dialog_update_title))
            .setMessage(getString(R.string.dialog_update_message))
            .setPositiveButton(getString(R.string.update_button)) { dialog, _ ->
                val name = textField.editText?.text.toString()
                if (name.isNotEmpty())
                    RealmUtils.updateItem(dummy, name)

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
