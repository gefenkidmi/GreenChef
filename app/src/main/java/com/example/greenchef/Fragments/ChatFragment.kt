package com.example.greenchef.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.Adapters.ChatAdapter
import com.example.greenchef.Objects.ChatMessage
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AiViewModel

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private val aiViewModel: AiViewModel by viewModels()

    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Initialize UI elements
        recyclerView = view.findViewById(R.id.recyclerView)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)

        // Set up RecyclerView
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatAdapter

        // Observe AI response
        aiViewModel.responseText.observe(viewLifecycleOwner, Observer { response ->
            if (response.isNotEmpty()) {
                chatMessages.add(ChatMessage(response, false)) // AI response
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)
            }
        })

        // Handle send button click
        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                sendMessage(userMessage)
            }
        }

        return view
    }

    private fun sendMessage(userMessage: String) {
        // Add user message to chat
        chatMessages.add(ChatMessage(userMessage, true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        recyclerView.scrollToPosition(chatMessages.size - 1)

        // Clear input field
        messageInput.text.clear()

        // Correctly call the ViewModel method
        val apiKey = getString(R.string.api_key)
        aiViewModel.generateContent(userMessage, apiKey)
    }
}


