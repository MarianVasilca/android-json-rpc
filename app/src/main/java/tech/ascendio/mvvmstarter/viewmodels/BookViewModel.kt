package tech.ascendio.mvvmstarter.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import tech.ascendio.mvvmstarter.data.repositories.BookRepository
import tech.ascendio.mvvmstarter.data.vo.Book

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

class BookViewModel(
        val bookRepository: BookRepository
) : ViewModel() {
    private val books: Flowable<List<Book>> = bookRepository.startListeningForBooks()

    fun observeCats(onNext: (List<Book>) -> Unit, onError: (Throwable) -> Unit = {}): Disposable =
            books.subscribeBy(onNext = onNext, onError = onError)

    fun fetch() = bookRepository.fetch()
}