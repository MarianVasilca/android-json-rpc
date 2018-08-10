package tech.ascendio.mvvmstarter.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import tech.ascendio.mvvm.ui.adapters.util.DataBoundListAdapter
import tech.ascendio.mvvmstarter.R
import tech.ascendio.mvvmstarter.data.vo.Message
import tech.ascendio.mvvmstarter.databinding.ChatMessageItemBinding

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

class MessageAdapter(
        private val callback: ((Message) -> Unit)?
) : DataBoundListAdapter<Message, ChatMessageItemBinding>(
        diffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                    oldItem.text == newItem.text
        }
) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ChatMessageItemBinding {
        val binding = DataBindingUtil
                .inflate<ChatMessageItemBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.chat_message_item,
                        parent,
                        false
                )
        binding.root.setOnClickListener {
            binding.message?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: ChatMessageItemBinding, item: Message, position: Int) {
        binding.message = item
        binding.position = position
    }
}