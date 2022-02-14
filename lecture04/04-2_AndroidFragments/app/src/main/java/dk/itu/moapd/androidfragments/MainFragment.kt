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
package dk.itu.moapd.androidfragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * A fragment to show the `Fragment 1` and a map of the Copenhagen area.
 *
 * The `MainActivity` has a `FrameLayout` area to replace dynamically the fragments used by this
 * project. You can use a bundle to share data between the main activity and this fragment.
 */
class MainFragment : Fragment() {

    /**
     * A set of private constants used in this class.
     */
    companion object {
        private val TAG = MainFragment::class.qualifiedName
    }

    /**
     * Called when a fragment is first attached to its context.`onCreate(Bundle)` will be called
     * after this.
     *
     * @param context The activity context.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "onAttach() called")
    }

    /**
     * Called to do initial creation of a fragment. This is called after `onAttach(Context)` and
     * before `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     *
     * Note that this can be called while the fragment's activity is still in the process of being
     * created. As such, you can not rely on things like the activity's content view hierarchy being
     * initialized at this point. If you want to do work once the activity itself is created, add a
     * `androidx.lifecycle.LifecycleObserver` on the activity's Lifecycle, removing it when it
     * receives the `Lifecycle.State#CREATED` callback.
     *
     * Any restored child fragments will be created before the base `Fragment.onCreate()` method
     * returns.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate() called")
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between `onCreate(Bundle)` and
     * `onViewCreated(View, Bundle)`. A default `View` can be returned by calling `Fragment(int)` in
     * your constructor. Otherwise, this method returns null.
     *
     * It is recommended to <strong>only</strong> inflate the layout in this method and move logic
     * that operates on the returned View to `onViewCreated(View, Bundle)`.
     *
     * If you return a `View` from here, you will later be called in `onDestroyView()` when the view
     * is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the
     *      fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be
     *      attached to. The fragment should not add the view itself, but this can be used to
     *      generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView() called")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    /**
     * Called immediately after `onCreateView(LayoutInflater, ViewGroup, Bundle)` has returned, but
     * before any saved state has been restored in to the view. This gives subclasses a chance to
     * initialize themselves once they know their view hierarchy has been completely created. The
     * fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated() called")
    }

    /**
     * Called when the Fragment is visible to the user. This is generally tied to
     * `Activity.onStart()` of the containing Activity's lifecycle.
     */
    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart() called")
    }

    /**
     * Called when the fragment is visible to the user and actively running. This is generally tied
     * to `Activity.onResume()` of the containing Activity's lifecycle.
     */
    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume() called")
    }

    /**
     * Called when the Fragment is no longer resumed. This is generally tied to `Activity.onPause()`
     * of the containing Activity's lifecycle.
     */
    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause() called")
    }

    /**
     * Called when the Fragment is no longer started. This is generally tied to `Activity.onStop()`
     * of the containing Activity's lifecycle.
     */
    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop() called")
    }

    /**
     * Called when the view previously created by `onCreateView()` has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created. This
     * is called after `onStop()` and before `onDestroy()`. It is called <em>regardless</em> of
     * whether `onCreateView()` returned a non-null view. Internally it is called after the view's
     * state has been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView() called")
    }

    /**
     * Called when the fragment is no longer in use. This is called after `onStop()` and before
     * `onDetach()`.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy() called")
    }

    /**
     * Called when the fragment is no longer attached to its activity. This is called after
     * `onDestroy()`.
     */
    override fun onDetach() {
        super.onDetach()
        Log.i(TAG, "onDetach() called")
    }

}
