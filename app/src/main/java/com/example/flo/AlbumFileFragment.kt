package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerAlbumFileBinding

class AlbumFileFragment: Fragment() {
    lateinit var binding: FragmentLockerAlbumFileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLockerAlbumFileBinding.inflate(inflater,container,false)
        return binding.root
    }
}