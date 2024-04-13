package com.example.flo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity(){

    lateinit var binding: ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 버튼을 누르면 MainActivity로 돌아가기
        binding.songDownIb.setOnClickListener{
            finish()
            //MainActivity 에서 String 타입의 값을 받으면 그 내용으로 Toast 메시지 띄우기
            Toast.makeText(this, intent.getStringExtra("title"), Toast.LENGTH_SHORT).show()
        }

        // 버튼을 누르면 일시정지 버튼으로 바꾸기
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(true)
        }

        // 버튼을 누르면 반복재생 버튼으로 바꾸기
        binding.songIsNotRepeatIv.setOnClickListener{
            setRepeatStatus(false)
        }
        binding.songIsRepeatIv.setOnClickListener{
            setRepeatStatus(true)
        }

        // 버튼을 누르면 랜덤재생 버튼으로 바꾸기
        binding.songIsNotRandomIv.setOnClickListener{
            setRandomStatus(false)
        }
        binding.songIsRandomIv.setOnClickListener{
            setRandomStatus(true)
        }

        // 미니플레이어에 있는 제목과 가수명 SongActivity에 전달하기
        if (intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }
    }

    // 버튼을 누르면 일시정지 버튼으로 바꾸기
    fun setPlayerStatus(isPlaying: Boolean){
        if (isPlaying){
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
        else{
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
    }

    // 버튼을 누르면 반복재생 버튼으로 바꾸기
    fun setRepeatStatus(isNotRepeating: Boolean){
        if (isNotRepeating){
            binding.songIsNotRepeatIv.visibility = View.VISIBLE
            binding.songIsRepeatIv.visibility = View.GONE
        }
        else{
            binding.songIsNotRepeatIv.visibility = View.GONE
            binding.songIsRepeatIv.visibility = View.VISIBLE
        }
    }

    // 버튼을 누르면 랜덤재생 버튼으로 바꾸기
    fun setRandomStatus(isNotRandom: Boolean){
        if (isNotRandom){
            binding.songIsNotRandomIv.visibility = View.VISIBLE
            binding.songIsRandomIv.visibility = View.GONE
        }
        else{
            binding.songIsNotRandomIv.visibility = View.GONE
            binding.songIsRandomIv.visibility = View.VISIBLE
        }
    }
}