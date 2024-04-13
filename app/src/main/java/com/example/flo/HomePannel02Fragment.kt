package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentHomePannel02Binding

class HomePannel02Fragment:Fragment() {
    lateinit var binding: FragmentHomePannel02Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePannel02Binding.inflate(inflater,container,false)
        return binding.root
    }
}