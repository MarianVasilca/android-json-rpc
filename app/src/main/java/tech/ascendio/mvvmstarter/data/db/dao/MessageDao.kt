package tech.ascendio.mvvmstarter.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import tech.ascendio.mvvmstarter.data.vo.Message

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
 * Interface for database access for [Message] related operations.
 */
@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messages: Array<Message>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messages: List<Message>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message): Long

    @Query("SELECT * FROM messages")
    fun listenForMessages(): Flowable<List<Message>>

    @Query("DELETE FROM messages")
    fun deleteMessages()

    @Query("DELETE FROM messages WHERE id = :id")
    fun deleteMessage(id: Long)
}