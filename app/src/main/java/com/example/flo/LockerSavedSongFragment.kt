package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerSavedSongBinding

class LockerSavedSongFragment: Fragment(){

    lateinit var binding: FragmentLockerSavedSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedSongBinding.inflate(inflater,container,false)
        return binding.root
    }
}