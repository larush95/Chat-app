package com.example.chatapp.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView.*
import com.example.chatapp.R
import com.example.chatapp.components.DeleteMessageDialogFragment
import com.example.chatapp.databinding.MessageBinding
import com.example.chatapp.databinding.OtherMessageBinding
import com.example.chatapp.model.Message
import com.example.chatapp.view.MainActivity
import com.example.chatapp.view.MainActivity.Companion.ANONYMOUS
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MessageAdapter(
    private val options: FirebaseRecyclerOptions<Message>,
    private val currentUserName: String?,
    private val activity: Activity
) :
    FirebaseRecyclerAdapter<Message, ViewHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_OWN) {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.other_message, parent, false)
            val binding = OtherMessageBinding.bind(view)
            OtherMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        if (options.snapshots[position].text != null) {
            if (options.snapshots[position].name != ANONYMOUS && currentUserName == options.snapshots[position].name && options.snapshots[position].name != null)
                (holder as MessageViewHolder).bind(model)
            else
                (holder as OtherMessageViewHolder).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].name != ANONYMOUS && currentUserName == options.snapshots[position].name && options.snapshots[position].name != null)
            VIEW_TYPE_OWN
        else
            VIEW_TYPE_OTHER
    }

    inner class MessageViewHolder(private val binding: MessageBinding) : ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.message.text = item.text
            binding.messengerName.text = item.name

            if (item.timeSent == null)
                return
            binding.timeSent.text = itemView.context.getString(
                R.string.two_dynamic_words,
                itemView.context.getString(R.string.sent),
                item.timeSent
            )
            binding.message.setOnClickListener {
                showTimeSent()
            }
            binding.message.setOnLongClickListener {
                if(activity is MainActivity)
                    activity.showDialogFragment(item.uid)
                return@setOnLongClickListener true
            }
        }

        private fun showTimeSent() {
            if (binding.timeSent.isVisible)
                binding.timeSent.visibility = GONE
            else
                binding.timeSent.visibility = VISIBLE
        }

    }

    inner class OtherMessageViewHolder(private val binding: OtherMessageBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.message.text = item.text
            binding.messengerName.text = item.name
            if (item.timeSent == null)
                return
            binding.timeSent.text = itemView.context.getString(
                R.string.two_dynamic_words,
                itemView.context.getString(R.string.sent),
                item.timeSent
            )
            binding.message.setOnClickListener {
                showTimeSent()
            }
        }

        private fun showTimeSent() {
            if (binding.timeSent.isVisible)
                binding.timeSent.visibility = GONE
            else
                binding.timeSent.visibility = VISIBLE
        }

    }

    companion object {
        const val VIEW_TYPE_OWN = 1
        const val VIEW_TYPE_OTHER = 2
    }

}
