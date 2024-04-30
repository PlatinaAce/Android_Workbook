package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import java.util.Timer

class SongActivity : AppCompatActivity(){

    lateinit var binding: ActivitySongBinding
    lateinit var song: Song
    lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer(song)

        // 버튼을 누르면 MainActivity로 돌아가기
        binding.songDownIb.setOnClickListener{
            finish()
            //MainActivity 에서 String 타입의 값을 받으면 그 내용으로 Toast 메시지 띄우기
            Toast.makeText(this, intent.getStringExtra("title"), Toast.LENGTH_SHORT).show()
        }

        // 버튼을 누르면 일시정지 버튼으로 바꾸기
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
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

    }

    // 재생 스레드 만들기
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
    }

    private fun initSong(){
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second",0),
                intent.getIntExtra("playTime",0),
                intent.getBooleanExtra("isPlaying",false)
            )
        }
        startTimer()
    }

    private fun setPlayer(song: Song){
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        setPlayerStatus(song.isPlaying)
    }

    // 버튼을 누르면 일시정지 버튼으로 바꾸기
    private fun setPlayerStatus(isPlaying: Boolean){
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying){
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
        else{
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
    }

    // 버튼을 누르면 반복재생 버튼으로 바꾸기
    private fun setRepeatStatus(isNotRepeating: Boolean){
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
    private fun setRandomStatus(isNotRandom: Boolean){
        if (isNotRandom){
            binding.songIsNotRandomIv.visibility = View.VISIBLE
            binding.songIsRandomIv.visibility = View.GONE
        }
        else{
            binding.songIsNotRandomIv.visibility = View.GONE
            binding.songIsRandomIv.visibility = View.VISIBLE
        }
    }

    // 재생 스레드 만들기

    private fun startTimer(){
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int,var isPlaying: Boolean = true):Thread() {

        private var second: Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (second >= playTime) {
                        break
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}