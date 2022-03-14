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

/**
 * An interface to implements listener methods for `RecyclerView` items.
 */
interface ItemClickListener  {

    /**
     * Implement this method to be executed when the user press an item in the `ReculerView` for a
     * short time.
     *
     * @param image An instance of `Image` class.
     */
    fun onItemClickListener(image: Image)

    /**
     * Implement this method to be executed when the user press an item in the `RecyclerView` for a
     * long time.
     *
     * @param image An instance of `Image` class.
     * @param viewHolder The selected view holder in the `RecyclerView`.
     */
    fun onItemLongClickListener(image: Image, viewHolder: CustomAdapter.ViewHolder)

}
