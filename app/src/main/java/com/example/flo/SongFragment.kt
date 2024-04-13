//Week 3 Lecture 18

package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment(){

    lateinit var binding: FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater,container,false)

        // 버튼을 누르면 MIX가 켜지는 버튼으로 바꾸기
        binding.songMixoffTg.setOnClickListener {
            setMixStatus(false)
        }
        binding.songMixonTg.setOnClickListener {
            setMixStatus(true)
        }

        return binding.root
    }

    fun setMixStatus(isNotMixed: Boolean){
        if (isNotMixed){
            binding.songMixoffTg.visibility = View.VISIBLE
            binding.songMixonTg.visibility = View.GONE
        }
        else{
            binding.songMixoffTg.visibility = View.GONE
            binding.songMixonTg.visibility = View.VISIBLE
        }
    }
}