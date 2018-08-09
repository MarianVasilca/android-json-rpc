package tech.ascendio.mvvmstarter.ui.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_main.*
import tech.ascendio.mvvmstarter.R
import tech.ascendio.mvvmstarter.data.repositories.JsonRpcRepository
import tech.ascendio.mvvmstarter.databinding.FragmentMainBinding
import tech.ascendio.mvvmstarter.di.InjectorUtils
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

    override fun onBoundViews(savedInstanceState: Bundle?) {
        initViewModels()
        tvSend.setOnClickListener { sendMessage(etMessage.text.toString()) }
    }

    private fun initViewModels() {
        val jsonRpcViewModelFactory = InjectorUtils.provideJsonRpcViewModelFactory()
        jsonRpcViewModel = ViewModelProviders.of(this, jsonRpcViewModelFactory).get(JsonRpcViewModel::class.java)
    }

    private fun sendMessage(message: String) {
        val startTime = System.currentTimeMillis()
        compositeDisposable += jsonRpcViewModel.sendMessage(message)
                .subscribeBy(onSuccess = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    Log.i(tag, "Time: ${System.currentTimeMillis() - startTime}")
                }, onError = {
                    Log.i(JsonRpcRepository.TAG, it.message)
                })
    }
}