package com.example.flo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentDetailBinding
import com.example.flo.databinding.FragmentLockerSavedSongBinding
import com.example.flo.databinding.FragmentSongBinding
import com.google.gson.Gson

class LockerSavedSongFragment : Fragment(), BottomSheetFragment.BottomSheetListener{
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
        binding.lockerSavedSongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdpater = LockerSavedSongRVAdapter()

        songRVAdpater.setMyItemClickListener(object : LockerSavedSongRVAdapter.MyItemClickListener{
            override fun onRemoveSong(songId: Int){
                songDB.songDao().updateIsLikeById(false, songId)
            }
        })

        binding.lockerSavedSongRv.adapter = songRVAdpater

        songRVAdpater.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)

        binding.lockerSelectAllTv.setOnClickListener {
            setSelectAllLayout(isSelect)
        }
    }

    private fun setSelectAllLayout(isSelect: Boolean) {
        if (!isSelect) {  // off 상태에서 클릭
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.setBottomSheetListener(this)  // 리스너 설정
            bottomSheetFragment.show(requireFragmentManager(), "BottomSheetDialog")
            binding.lockerSelectAllTv.text = "선택해제"
            binding.lockerSelectAllTv.setTextColor(Color.parseColor("#0019F4"))
            binding.lockerSelectAllImgIv.setImageResource(R.drawable.btn_playlist_select_on)
            this.isSelect = true
        } else {
            binding.lockerSelectAllTv.text = "전체선택"
            binding.lockerSelectAllTv.setTextColor(Color.parseColor("000000"))
            binding.lockerSelectAllImgIv.setImageResource(R.drawable.btn_playlist_select_off)
            this.isSelect = false
        }
    }
    override fun onButtonClicked() {
        binding.lockerSelectAllTv.text = "전체선택"
        binding.lockerSelectAllTv.setTextColor(Color.parseColor("#000000"))
        binding.lockerSelectAllImgIv.setImageResource(R.drawable.btn_playlist_select_off)
        this.isSelect = false
    }
}