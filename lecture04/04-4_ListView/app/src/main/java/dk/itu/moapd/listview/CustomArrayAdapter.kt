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
package dk.itu.moapd.listview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * A class to customize an adapter with a `ViewHolder` to populate a dummy dataset into a `ListView`.
 */
class CustomArrayAdapter(context: Context, private var resource: Int, data: List<DummyModel>) :
    ArrayAdapter<DummyModel>(context, R.layout.list_item, data) {

    /**
     * A set of private constants used in this class.
     */
    companion object {
        private val TAG = CustomArrayAdapter::class.qualifiedName
    }

    /**
     * An internal view holder class used to represent the layout that shows a single `DummyModel`
     * instance in the `ListView`.
     */
    private class ViewHolder(view: View) {
        val title: TextView = view.findViewById(R.id.title)
        val secondaryText: TextView = view.findViewById(R.id.secondary_text)
        val supportingText: TextView = view.findViewById(R.id.supporting_text)
    }

    /**
     * Get the `View` instance with information about a selected `DummyModel` from the dataset.
     *
     * @param position The position of the specified item.
     * @param convertView The current view holder.
     * @param parent The parent view which will group the view holder.
     *
     * @return A new view holder populated with the selected `DummyModel` data.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder

        // The old view should be reused, if possible. You should check that this view is non-null
        // and of an appropriate type before using.
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
        } else
            viewHolder = view.tag as ViewHolder

        // Get the selected item in the dataset.
        val dummy = getItem(position)
        Log.i(TAG, "Populate an item at position: $position")

        // Populate the view holder with the selected `DummyModel` data.
        viewHolder.title.text = parent.context.getString(
            R.string.title, dummy?.artistName)
        viewHolder.secondaryText.text = parent.context.getString(
            R.string.secondary_text, dummy?.year, dummy?.country)
        viewHolder.supportingText.text = parent.context.getString(
            R.string.supporting_text, dummy?.text)

        // Set the new view holder and return the view object.
        view?.tag = viewHolder
        return view!!
    }

}
