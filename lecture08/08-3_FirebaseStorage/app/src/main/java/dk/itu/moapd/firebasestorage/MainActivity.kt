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
package dk.itu.moapd.firebasestorage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.firebasestorage.databinding.ActivityMainBinding
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.ThumbnailUtils
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import java.util.*
import android.graphics.Bitmap
import android.net.Uri
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream

/**
 * An activity class with methods to manage the main activity of Firebase Storage application.
 */
class MainActivity : AppCompatActivity(), ItemClickListener {

    /**
     * This object launches a new activity and receives back some result data.
     */
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        galleryResult(result)
    }

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
     * The entry point of the Firebase Storage SDK.
     */
    private lateinit var storage: FirebaseStorage

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

        // Initialize Firebase Auth and connect to the Firebase Realtime Database and Firebase
        // Storage.
        auth = FirebaseAuth.getInstance()
        database = Firebase.database(DATABASE_URL).reference
        storage = Firebase.storage(BUCKET_URL)

        // Create the search query.
        val query = database.child("images")
            .child(auth.currentUser?.uid ?: "None")
            .orderByChild("createdAt")

        // A class provide by FirebaseUI to make a query in the database to fetch appropriate data.
        val options = FirebaseRecyclerOptions.Builder<Image>()
            .setQuery(query, Image::class.java)
            .setLifecycleOwner(this)
            .build()

        // Create the custom adapter to bind a list of images.
        adapter = CustomAdapter(this, options)

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Firebase Sign Out.
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.image -> {
                    val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "image/*"
                    }
                    galleryLauncher.launch(galleryIntent)
                    true
                }
                R.id.subtitle -> {
                    adapter.toggleDisplayDate()
                    true
                }
                R.id.more -> {
                    auth.signOut()
                    startLoginActivity()
                    true
                }
                else -> false
            }
        }

        // Define the add button behavior.
        binding.floatingActionButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            galleryLauncher.launch(galleryIntent)
        }

        // Define the recycler view layout manager.
        val padding = 2
        val columns = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            else -> 4
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, columns)
        binding.recyclerView.setPadding(padding, padding, padding, padding)
        binding.recyclerView.clipToPadding = false
        binding.recyclerView.clipChildren = false
        binding.recyclerView.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.set(padding, padding, padding, padding)
            }
        })
        binding.recyclerView.adapter = adapter

        // Inflate the user interface into the current activity.
        setContentView(binding.root)
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
     * This method will be executed when the user press an item in the `ReculerView` for a short
     * time.
     *
     * @param image An instance of `Image` class.
     */
    override fun onItemClickListener(image: Image) {
        val intent = Intent(this, ImageActivity::class.java).apply {
            putExtra("URL", image.url)
        }
        startActivity(intent)
    }

    /**
     * This method will be executed when the user press an item in the `RecyclerView` for a long
     * time.
     *
     * @param image An instance of `Image` class.
     * @param viewHolder The selected view holder in the `RecyclerView`.
     */
    override fun onItemLongClickListener(image: Image, viewHolder: CustomAdapter.ViewHolder) {
        MaterialAlertDialogBuilder(this)
            .setMessage(resources.getString(R.string.delete))
            .setNegativeButton(resources.getString(R.string.decline)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                val adapter = binding.recyclerView.adapter as CustomAdapter
                adapter.getRef(viewHolder.adapterPosition).removeValue().addOnSuccessListener {
                    val thumbnailRef = storage.reference.child("${image.path}_thumbnail")
                    thumbnailRef.delete().addOnSuccessListener {
                        val imageRef = storage.reference.child("${image.path}")
                        imageRef.delete().addOnSuccessListener {
                            Toast.makeText(this, "Item deleted successfully",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .show()
    }

    /**
     * This method saves a reference of uploaded image in the database. The Firebase Storage does
     * NOT have a option to observe changes in the bucket an automatically updates the application.
     * We must use a database to have this feature in our application.
     *
     * @param url The public URL of uploaded image.
     * @param path The private URL of uploaded image on Firebase Storage.
     */
    private fun saveImageInDatabase(url: String, path: String) {
        val timestamp = System.currentTimeMillis()
        val image = Image(url, path, timestamp)

        val uid = database.child("images")
            .child(auth.currentUser?.uid!!)
            .push()
            .key

        database.child("images")
            .child(auth.currentUser?.uid!!)
            .child(uid!!)
            .setValue(image)
    }

    /**
     * When the second activity finishes (i.e., the photo gallery intent), it returns a result to
     * this activity. If the user selects an image correctly, we can get a reference of the selected
     * image and send it to the Firebase Storage.
     *
     * @param result A container for an activity result as obtained form `onActivityResult()`.
     */
    private fun galleryResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            // Create the folder structure save the selected image in the bucket.
            val uid = auth.currentUser?.uid
            val filename = UUID.randomUUID().toString()
            val imageRef = storage.reference.child("images/$uid/$filename")
            val thumbnailRef = storage.reference.child("images/$uid/${filename}_thumbnail")

            // Code for showing progress bar while uploading.
            binding.progressBar.visibility = View.VISIBLE

            // Upload the selected image.
            val imageUri = result.data?.data!!
            imageRef.putFile(imageUri).addOnSuccessListener {
                val image = it.metadata?.reference?.downloadUrl

                // Upload the thumbnail image.
                val thumbnailUri = createThumbnail(imageUri)
                thumbnailRef.putFile(thumbnailUri).addOnSuccessListener {

                    // Save the image reference in the database.
                    image?.addOnSuccessListener { url ->
                        saveImageInDatabase(url.toString(), imageRef.path)
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * This method creates a squared thumbnail of the uploaded image. We are going to use the
     * thumbnail to show the images into the `RecyclerView`.
     *
     * @param uri The immutable URI reference of uploaded image.
     * @param size The image resolution used to create the thumbnail (Default: 300).
     *
     * @return The immutable URI reference of created thumbnail image.
     */
    private fun createThumbnail(uri: Uri, size: Int = 300): Uri {
        val decode = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        val thumbnail = ThumbnailUtils.extractThumbnail(
            decode, size, size, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        return getImageUri(thumbnail)
    }

    /**
     * This method saves the bitmap in the temporary folder and return its immutable URI reference.
     *
     * @param image The thumbnail bitmap created in memory.
     *
     * @return The immutable URI reference of created thumbnail image.
     */
    private fun getImageUri(image: Bitmap): Uri {
        val file = File(cacheDir, "thumbnail")
        val outStream = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.close()
        return Uri.fromFile(file)
    }

}
