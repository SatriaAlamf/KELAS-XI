package com.example.whatsapp.ui.communities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.whatsapp.databinding.FragmentCommunitiesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunitiesFragment : Fragment() {

    private var _binding: FragmentCommunitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // TODO: Implement communities functionality
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}