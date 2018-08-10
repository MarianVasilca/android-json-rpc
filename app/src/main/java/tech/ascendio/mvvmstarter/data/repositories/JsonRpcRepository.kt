package tech.ascendio.mvvmstarter.data.repositories

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import tech.ascendio.mvvmstarter.data.api.JsonRpcService
import tech.ascendio.mvvmstarter.data.db.dao.MessageDao
import tech.ascendio.mvvmstarter.data.vo.Message
import tech.ascendio.mvvmstarter.utilities.schedulers.MainScheduler
import tech.ascendio.mvvmstarter.utilities.schedulers.Scheduler

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

class JsonRpcRepository constructor(
        private val jsonRpcService: JsonRpcService,
        private val mainScheduler: MainScheduler,
        private val networkScheduler: Scheduler,
        private val ioScheduler: Scheduler,
        private val messageDao: MessageDao
) {

    fun startListeningForMessages(): Flowable<List<Message>> =
            messageDao.listenForMessages()
                    .distinctUntilChanged()
                    .observeOn(mainScheduler.asRxScheduler())
                    .replay(1)
                    .autoConnect(0)

    fun sendMessage(message: String) {
        createSaveBooksTask(message).subscribeBy(onSuccess = {
            // Success
        }, onError = { throwable ->
            Log.i(TAG, "Fetching failed", throwable)
        })
    }

    private fun createSaveBooksTask(message: String): Single<Unit> = Single.create { emitter ->
        jsonRpcService.echo(message)
                .subscribeOn(networkScheduler.asRxScheduler())
                .observeOn(ioScheduler.asRxScheduler())
                .subscribeBy(onSuccess = {
                    if (it == null) {
                        emitter.onSuccess(Unit)
                        return@subscribeBy
                    }
                    messageDao.insert(Message(message, 1))
                    messageDao.insert(Message(it, 0))
                    emitter.onSuccess(Unit)
                }, onError = {
                    emitter.onError(it)
                })
    }

    companion object {
        const val TAG = "JsonRpcRepository"

        // For Singleton instantiation
        @Volatile
        private var instance: JsonRpcRepository? = null

        fun getInstance(jsonRpcService: JsonRpcService, mainScheduler: MainScheduler,
                        networkScheduler: Scheduler, iokScheduler: Scheduler, messageDao: MessageDao) =
                instance ?: synchronized(this) {
                    instance ?: JsonRpcRepository(jsonRpcService, mainScheduler, networkScheduler,
                            iokScheduler, messageDao).also { instance = it }
                }
    }
}