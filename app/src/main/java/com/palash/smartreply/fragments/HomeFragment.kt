package com.palash.smartreply.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplyGenerator
import com.google.mlkit.nl.smartreply.SmartReplySuggestion
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import com.palash.smartreply.R
import com.palash.smartreply.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var conversations = ArrayList<TextMessage>()
    var userUID = "123123"
    lateinit var smartReplayGenerator: SmartReplyGenerator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conversations = ArrayList()
        smartReplayGenerator = SmartReply.getClient()
        binding.btnSend.setOnClickListener {
            val message = binding.edtMessage.text.toString().trim()
            conversations.add(
                TextMessage.createForRemoteUser(
                    message,
                    System.currentTimeMillis(),
                    userUID
                )
            )

            smartReplayGenerator.suggestReplies(conversations).addOnSuccessListener {
                if (it.status == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                    binding.txtReply.text = "STATUS_NOT_SUPPORTED_LANGUAGE"
                } else if (it.status == SmartReplySuggestionResult.STATUS_SUCCESS) {
                    var reply = ""
                    for (suggestion: SmartReplySuggestion in it.suggestions) {
                        reply = reply + suggestion.text + "\n"
                    }

                    binding.txtReply.text = reply
                }
            }.addOnFailureListener {
                binding.txtReply.text = "Error ${it.message}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}