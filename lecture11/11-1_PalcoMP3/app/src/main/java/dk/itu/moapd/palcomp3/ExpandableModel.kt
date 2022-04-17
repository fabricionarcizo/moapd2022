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
package dk.itu.moapd.palcomp3

/**
 * A model class with all parameters to represent an `ExpandableModel` object in the Palco MP3
 * application.
 */
class ExpandableModel {

    /**
     * The artist object represents the parent instance in the RecyclerView.
     */
    lateinit var artistParent: ArtistModel

    /**
     * The song object represents the child instance of an artist in the RecyclerView.
     */
    lateinit var songChild : SongModel

    /**
     * A set of static attributes used in this class.
     */
    companion object {
        const val PARENT = 1
        const val CHILD = 2
    }

    /**
     * Defined the type of the RecyclerView item.
     * 1 - Parent
     * 2 - Child
     */
    var type : Int

    /**
     * A boolean flag to inform if the `RecyclerView` item is expanded.
     */
    var isExpanded : Boolean

    /**
     * A boolean flag to inform if the `RecyclerView` item is shown.
     */
    private var isCloseShown : Boolean

    /**
     * The alternative constructor used to create a parent `RecyclerView` item.
     */
    constructor(type: Int, artistParent: ArtistModel, isExpanded: Boolean = false,
                isCloseShown: Boolean = false) {
        this.type = type
        this.artistParent = artistParent
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    /**
     * The alternative constructor used to create a child `RecyclerView` item.
     */
    constructor(type: Int, songChild: SongModel, isExpanded: Boolean = false,
                isCloseShown: Boolean = false) {
        this.type = type
        this.songChild = songChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

}
