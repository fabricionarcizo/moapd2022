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

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A view model sensitive to changes in the `MainActivity` UI components.
 */
class MainViewModel : ViewModel() {

    /**
     * The current song playing in the main activity.
     */
    private var song = MutableLiveData<SongModel>()

    /**
     * This method will be executed when the user interacts with any player control button. It sets
     * the new song in the LiveData instance.
     *
     * @param song A instance of `SongModel` class.
     */
    fun onSongChanged(song: SongModel) {
        // If the user selects a different song, stop playing the previous one.
        if (song != this.song.value)
            this.song.value?.isPlaying = false

        // Update the current song.
        this.song.value = song
    }

    /**
     * The current player control button from the RecyclerView.
     */
    private var imageView = MutableLiveData<ImageView>()

    /**
     * This method returns the current player control button.
     *
     * @param imageView The latest player control button.
     */
    fun onImageViewChanged(imageView: ImageView) {
        // Disable the previous song.
        this.imageView.value?.setImageDrawable(
            AppCompatResources.getDrawable(imageView.context,
                R.drawable.ic_baseline_play_circle_outline_60
            )
        )

        // Update the current player control button.
        this.imageView.value = imageView
    }

}
