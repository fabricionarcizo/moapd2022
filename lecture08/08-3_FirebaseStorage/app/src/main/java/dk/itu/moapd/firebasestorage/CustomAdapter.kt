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

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class to customize an adapter with a `ViewHolder` to populate an Image dataset into a
 * `RecyclerView`.
 */
class CustomAdapter(private val itemClickListener: ItemClickListener,
                    options: FirebaseRecyclerOptions<Image>) :
    FirebaseRecyclerAdapter<Image, CustomAdapter.ViewHolder>(options) {

    /**
     * This attribute controls the image subtitle visibility.
     */
    private var displayDate = false

    /**
     * An internal view holder class used to represent the layout that shows a single `String`
     * instance in the `RecyclerView`.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view)
        val textView: TextView = view.findViewById(R.id.text_view)
    }

    /**
     * Called when the `RecyclerView` needs a new `ViewHolder` of the given type to represent an
     * item.
     *
     * This new `ViewHolder` should be constructed with a new `View` that can represent the items of
     * the given type. You can either create a new `View` manually or inflate it from an XML layout
     * file.
     *
     * The new `ViewHolder` will be used to display items of the adapter using
     * `onBindViewHolder(ViewHolder, int, List)`. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the `View` to
     * avoid unnecessary `findViewById(int)` calls.
     *
     * @param parent The `ViewGroup` into which the new `View` will be added after it is bound to an
     *      adapter position.
     * @param viewType The view type of the new `View`.
     *
     * @return A new `ViewHolder` that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called by the `RecyclerView` to display the data at the specified position. This method
     * should update the contents of the `itemView()` to reflect the item at the given position.
     *
     * Note that unlike `ListView`, `RecyclerView` will not call this method again if the position
     * of the item changes in the data set unless the item itself is invalidated or the new position
     * cannot be determined. For this reason, you should only use the `position` parameter while
     * acquiring the related data item inside this method and should not keep a copy of it. If you
     * need the position of an item later on (e.g., in a click listener), use
     * `getBindingAdapterPosition()` which will have the updated adapter position.
     *
     * Override `onBindViewHolder(ViewHolder, int, List)` instead if Adapter can handle efficient
     * partial bind.
     *
     * @param holder The `ViewHolder` which should be updated to represent the contents of the item
     *      at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @param image An instance of `image` class.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int, image: Image) {

        // Get the public thumbnail URL.
        val storage = Firebase.storage(BUCKET_URL)
        val imageRef = storage.reference.child("${image.path}_thumbnail")

        // Download and set an image into the ImageView.
        imageRef.downloadUrl.addOnSuccessListener {
            Glide.with(holder.itemView.context)
                .load(it)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(holder.imageView)
        }

        Log.i(TAG(), "Populate an item at position: $position")

        // Bind the view holder with the selected `String` data.
        holder.apply {
            // Set the TextView visibility.
            textView.text = image.createdAt?.toDateString()
            textView.isVisible = displayDate

            // Listen for short clicks in the current view holder.
            itemView.setOnClickListener {
                itemClickListener.onItemClickListener(image)
            }

            // Listen for long clicks in the current view holder.
            itemView.setOnLongClickListener {
                itemClickListener.onItemLongClickListener(image, holder)
                true
            }
        }
    }

    /**
     * This method changes the status of image subtitle visibility and updates the data shown in the
     * `RecyclerView`.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun toggleDisplayDate() {
        displayDate = !displayDate
        notifyDataSetChanged()
    }

    /**
     * Return the timestamp as a `String`.
     *
     * @return The timestamp formatted as a `String` using the default locale.
     */
    private fun Long.toDateString() : String {
        val date = Date(this)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }

}
