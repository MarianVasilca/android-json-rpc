package tech.ascendio.mvvmstarter.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import tech.ascendio.mvvmstarter.data.repositories.JsonRpcRepository
import tech.ascendio.mvvmstarter.data.vo.Message
import tech.ascendio.mvvmstarter.utilities.network.NetworkState

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

class JsonRpcViewModel(
        private val jsonRpcRepository: JsonRpcRepository
) : ViewModel() {

    private val messages: Flowable<List<Message>> = jsonRpcRepository.startListeningForMessages()

    fun observeMessages(onNext: (List<Message>) -> Unit, onError: (Throwable) -> Unit = {}): Disposable =
            messages.subscribeBy(onNext = onNext, onError = onError)

    val message = MutableLiveData<String>()
    fun sendMessage(msg: String) {
        message.value = msg
    }

    val refreshState: LiveData<NetworkState> = Transformations.switchMap(message) {
        jsonRpcRepository.sendMessage(it)
    }
}