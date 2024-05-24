package com.example.flo

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity(){

    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var song : Song = Song()
    var nowPos = 0

    private var resumePosition = 0

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()
    }

    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()
        songs[nowPos].second = (songs[nowPos].playTime * binding.songProgressSb.progress) / 100000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("second", songs[nowPos].second)
        editor.apply()
    }

    private fun saveSongData() {
        songs[nowPos].second = (binding.songProgressSb.progress * songs[nowPos].playTime / 100) / 1000    // 초 단위로 저장

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()   // spf를 사용하기 위한 에디터

        editor.putInt("songId", songs[nowPos].id)

        editor.apply()
    }

    // 재생 스레드 만들기
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        // 미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer?.release()
        // 미디어 플레이어 해제
        mediaPlayer = null
    }

    private fun initSong(){
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        nowPos = getPlayingSongPosition(songId)
        Log.d("now Song ID", songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener(){
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

        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }

        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
            if (songs[nowPos].isLike == true){
                CustomSnackbar.make(binding.root, "좋아요 한 곡에 담겼습니다.").show()
            }
            else{
                CustomSnackbar.make(binding.root, "좋아요 한 곡이 취소되었습니다.").show()
            }
        }
    }

    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if (!isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun moveSong(direct: Int){
        if (nowPos + direct < 0){
            CustomSnackbar.make(binding.root, "처음 곡입니다.").show()
            return
        }
        if (nowPos + direct >= songs.size){
            CustomSnackbar.make(binding.root, "마지막 곡입니다").show()
            return
        }

        nowPos += direct

        timer.interrupt()
        resetMusic()
        startTimer()

        // 미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer?.release()
        // 미디어 플레이어 해제
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }

    private fun setPlayer(song: Song){
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 100000 / song.playTime)
        binding.songAlbumIv.setImageResource(song.coverImg!!)

        val musicResourceId = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, musicResourceId)

        if (mediaPlayer == null) {
            resetMusic()
        } else if (song.second != 0) {
            mediaPlayer?.seekTo(song.second * 1000 + 1200)
        }

        if (song.isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        startTimer()

        setPlayerStatus(song.isPlaying)
    }

    // 버튼을 누르면 일시정지 버튼으로 바꾸기
    private fun setPlayerStatus(isPlaying: Boolean){
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying){
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        }
        else{
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
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
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int,var isPlaying: Boolean = true):Thread() {

        private var second: Int = songs[nowPos].second
        private var mills: Float = (songs[nowPos].second * 1000).toFloat()

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (second >= playTime) {
                        second = 0
                        mills = 0f
                        resetMusic()
                        if (binding.songIsRepeatIv.visibility == View.VISIBLE){
                            runOnUiThread{
                                setPlayerStatus(true)
                            }
                        }
                        else{
                            runOnUiThread {
                                setPlayerStatus(false)
                            }
                        }
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

    private fun resetMusic() {
        mediaPlayer?.reset()
        val music = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
    }
}