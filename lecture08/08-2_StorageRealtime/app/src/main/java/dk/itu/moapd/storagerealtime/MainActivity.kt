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
package dk.itu.moapd.storagerealtime

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.storagerealtime.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Storage Realtime application.
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
     * The entry point of the Firebase Authentication SDK.
     */
    private lateinit var auth: FirebaseAuth

    /**
     * A Firebase reference represents a particular location in your Database and can be used for
     * reading or writing data to that Database location.
     */
    private lateinit var database: DatabaseReference

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

        // Initialize Firebase Auth and connect to the Firebase Realtime Database.
        auth = FirebaseAuth.getInstance()
        database = Firebase.database(DATABASE_URL).reference

        // Enable offline capabilities.
        database.keepSynced(true)

        // Create the search query.
        val query = database.child("dummies")
            .child(auth.currentUser?.uid ?: "None")
            .orderByChild("createdAt")

        // A class provide by FirebaseUI to make a query in the database to fetch appropriate data.
        val options = FirebaseRecyclerOptions.Builder<Dummy>()
            .setQuery(query, Dummy::class.java)
            .setLifecycleOwner(this)
            .build()

        // Create the custom adapter to bind a list of dummy objects.
        adapter = CustomAdapter(this, options)

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Firebase Sign Out.
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.more -> {
                    auth.signOut()
                    startLoginActivity()
                    true
                }
                else -> false
            }
        }

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
                adapter.getRef(viewHolder.adapterPosition).removeValue()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        // Inflate the user interface into the current activity.
        val view = binding.root
        setContentView(view)
    }

    /**
     * Called after `onCreate()` method; or after `onRestart()` method when the activity had been
     * stopped, but is now again being displayed to the user. It will usually be followed by
     * `onResume()`. This is a good place to begin drawing visual elements, running animations, etc.
     *
     * You can call `finish()` from within this function, in which case `onStop()` will be
     * immediately called after `onStart()` without the lifecycle transitions in-between
     * (`onResume()`, `onPause()`, etc) executing.
     *
     * <em>Derived classes must call through to the super class's implementation of this method. If
     * they do not, an exception will be thrown.</em>
     */
    override fun onStart() {
        super.onStart()

        // Check if the user is not logged and redirect her/him to the LoginActivity.
        if (auth.currentUser == null)
            startLoginActivity()
    }

    /**
     * This method starts the login activity which allows the user log in or sign up to the Firebase
     * Authentication application.
     *
     * Before accessing the main activity, the user must log in the application through a Firebase
     * Auth backend service. The method starts a new activity using explicit intent and used the
     * method `finish()` to disable back button.
     */
    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * This method will be executed when the user press an item in the `RecyclerView` for a long
     * time.
     *
     * @param dummy An instance of `Dummy` class.
     * @param position The selected position in the `RecyclerView`.
     */
    override fun onItemClickListener(dummy: Dummy, position: Int) {
        // Inflate Custom alert dialog view
        customAlertDialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_data, binding.root, false)

        // Launching the custom alert dialog
        launchUpdateAlertDialog(dummy, position)
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
                if (name.isNotEmpty()) {
                    val timestamp = System.currentTimeMillis()
                    val dummy = Dummy(name, timestamp, timestamp)

                    val uid = database.child("dummies")
                        .child(auth.currentUser?.uid!!)
                        .push()
                        .key

                    database.child("dummies")
                        .child(auth.currentUser?.uid!!)
                        .child(uid!!)
                        .setValue(dummy)
                }

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
     * @param position The selected position in the `RecyclerView`.
     */
    private fun launchUpdateAlertDialog(dummy: Dummy, position: Int) {
        val textField = customAlertDialogView.findViewById<TextInputLayout>(R.id.name_text_field)
        textField.editText?.setText(dummy.name)

        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(getString(R.string.dialog_update_title))
            .setMessage(getString(R.string.dialog_update_message))
            .setPositiveButton(getString(R.string.update_button)) { dialog, _ ->
                val name = textField.editText?.text.toString()
                if (name.isNotEmpty()) {
                    dummy.name = name
                    dummy.updatedAt = System.currentTimeMillis()

                    val adapter = binding.recyclerView.adapter as CustomAdapter
                    adapter.getRef(position).setValue(dummy)
                }

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
