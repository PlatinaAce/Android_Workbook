package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSavedSongBinding
import com.google.gson.Gson

class LockerSavedSongFragment : Fragment() {
    lateinit var binding: FragmentLockerSavedSongBinding
    lateinit var songDB: SongDatabase
    var isSelect: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedSongBinding.inflate(inflater, container, false)
        songDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }
    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.lockerMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdpater = LockerSavedSongRVAdapter()

        songRVAdpater.setMyItemClickListener(object : LockerSavedSongRVAdapter.MyItemClickListener{
            override fun onRemoveSong(songId: Int){
                songDB.songDao().updateIsLikeById(false, songId)
            }
        })

        binding.lockerMusicAlbumRv.adapter = songRVAdpater

        songRVAdpater.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)


    }
}