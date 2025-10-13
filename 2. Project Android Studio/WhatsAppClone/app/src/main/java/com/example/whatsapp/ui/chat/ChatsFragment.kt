package com.example.whatsapp.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.databinding.FragmentChatsBinding
import com.example.whatsapp.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatsAdapter: ChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeConversations()
    }

    private fun setupRecyclerView() {
        chatsAdapter = ChatsAdapter { conversation ->
            // Navigate to chat detail
            // TODO: Implement navigation to ChatDetailActivity
        }
        
        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.searchCard.setOnClickListener {
            // TODO: Navigate to search activity
        }

        binding.fabNewChat.setOnClickListener {
            // TODO: Navigate to new chat activity
        }
    }

    private fun observeConversations() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.conversations.collect { conversations ->
                if (conversations.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvChats.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvChats.visibility = View.VISIBLE
                    chatsAdapter.submitList(conversations)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}