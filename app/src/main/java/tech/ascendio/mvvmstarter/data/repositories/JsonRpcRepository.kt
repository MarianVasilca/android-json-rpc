package tech.ascendio.mvvmstarter.data.repositories

import tech.ascendio.mvvmstarter.data.api.JsonRpcService
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
        private val networkScheduler: Scheduler
) {

    fun sendMessage(message: String) = jsonRpcService.echo(message)
            .subscribeOn(networkScheduler.asRxScheduler())
            .observeOn(mainScheduler.asRxScheduler())!!

    companion object {
        const val TAG = "JsonRpcRepository"

        // For Singleton instantiation
        @Volatile
        private var instance: JsonRpcRepository? = null

        fun getInstance(jsonRpcService: JsonRpcService, mainScheduler: MainScheduler,
                        networkScheduler: Scheduler) =
                instance ?: synchronized(this) {
                    instance
                            ?: JsonRpcRepository(jsonRpcService, mainScheduler, networkScheduler).also { instance = it }
                }
    }
}