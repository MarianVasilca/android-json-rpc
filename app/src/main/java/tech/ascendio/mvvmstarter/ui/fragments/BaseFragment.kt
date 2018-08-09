package tech.ascendio.mvvmstarter.ui.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import tech.ascendio.mvvmstarter.ui.activities.MainActivity
import tech.ascendio.mvvmstarter.utilities.AutoClearedValue

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    @get:LayoutRes
    internal abstract val layoutResource: Int

    // tag is not a companion object because it should be used only from a BaseFragment reference
    internal abstract val tag: String

    lateinit var viewDataBinding: T
    @VisibleForTesting
    var binding: AutoClearedValue<T>? = null
    protected val compositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.i(tag, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(tag, "onCreateView")
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResource, container, false)
        binding = AutoClearedValue(this)
        cancelFragmentClickEvents(viewDataBinding.root)
        return viewDataBinding.root
    }

    /**
     * When two fragments are overlapping into a container, the one that's behind can still receive
     * click events if the one that's on front does not become clickable.
     */
    private fun cancelFragmentClickEvents(view: View) {
        view.isClickable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.focusable = View.FOCUSABLE
        }
    }

    /**
     * Using Kotlin Extensions we can achieve View Binding after view is created. Make sure that
     * every view method call is done after this method is called.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(tag, "onViewCreated")
        onBoundViews(savedInstanceState)
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
        Log.i(tag, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(tag, "onDetach")
    }

    protected fun onBackPressed() {
        activity?.onBackPressed()
    }

    private fun getMainActivity(): MainActivity {
        return activity as MainActivity
    }

    private fun showSnackBar(message: String) {
        val view = view
        if (view != null) Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    protected fun showSnackBar(@StringRes stringResourceId: Int) {
        showSnackBar(getString(stringResourceId))
    }

    internal abstract fun onBoundViews(savedInstanceState: Bundle?)
}