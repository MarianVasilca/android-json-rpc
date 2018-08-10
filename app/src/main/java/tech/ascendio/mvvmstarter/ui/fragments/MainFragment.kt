package tech.ascendio.mvvmstarter.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_main.*
import tech.ascendio.mvvmstarter.R
import tech.ascendio.mvvmstarter.databinding.FragmentMainBinding
import tech.ascendio.mvvmstarter.di.InjectorUtils
import tech.ascendio.mvvmstarter.ui.adapters.MessageAdapter
import tech.ascendio.mvvmstarter.utilities.autoCleared
import tech.ascendio.mvvmstarter.utilities.network.Status
import tech.ascendio.mvvmstarter.viewmodels.JsonRpcViewModel

/*
 * Copyright (C) 2018 Marian Vasilca@Ascendio TechVision
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A fragment representing the start destination of the application.
 */
class MainFragment : BaseFragment<FragmentMainBinding>() {
    override val layoutResource: Int
        get() = R.layout.fragment_main
    override val tag: String
        get() = "MainFragment"

    private lateinit var jsonRpcViewModel: JsonRpcViewModel
    private var adapter by autoCleared<MessageAdapter>()

    override fun onBoundViews(savedInstanceState: Bundle?) {
        setAdapter()
        initViewModels()
        tvSend.setOnClickListener {
            jsonRpcViewModel.sendMessage(etMessage.text.toString())
            etMessage.text.clear()
        }
        subscribeUI()
    }

    private fun setAdapter() {
        adapter = MessageAdapter { }
        rvMessages.adapter = adapter
    }

    private fun initViewModels() {
        val jsonRpcViewModelFactory = InjectorUtils.provideJsonRpcViewModelFactory(requireContext())
        jsonRpcViewModel = ViewModelProviders.of(this, jsonRpcViewModelFactory).get(JsonRpcViewModel::class.java)
    }

    private fun subscribeUI() {
        compositeDisposable += jsonRpcViewModel.observeMessages(onNext = { messages ->
            Log.i(tag, "Size: ${messages.size}")
            adapter.submitList(messages)
            rvMessages.smoothScrollToPosition(messages.lastIndex)
        }, onError = {

        })

        jsonRpcViewModel.refreshState.observe(this, Observer {
            when (it.status) {
                Status.RUNNING -> Log.i(tag, "Loading")
                Status.SUCCESS -> Log.i(tag, "Success")
                Status.FAILED -> Log.i(tag, "Failed: ${it.msg}")
            }
        })
    }
}