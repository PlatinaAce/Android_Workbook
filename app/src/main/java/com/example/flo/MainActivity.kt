package com.example.flo

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private var timer: Timer? = null
    private var song: Song = Song()
    private var gson: Gson = Gson()
    val songs = arrayListOf<Song>()
    val albums = arrayListOf<Album>()
    private var nowPos = 0
    lateinit var songDB: SongDatabase
    lateinit var albumDB: SongDatabase

    fun updateMainPlayerCl(albumId: Int){
        startTimer()
        nowPos = getPlayingSongPosition(albumId)
        song = songs[nowPos]  //데이터베이스에서 데이터 찾기
        song.second = 0
        startTimer()
        resetMedia()
        playSong()
        binding.mainMiniplayerTitleTv.text=song.title
        binding.mainMiniplayerSingerTv.text=song.singer
        binding.mainMiniplayerProgressSb.progress=0

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//      플로 스플래시 화면 구현하기
        setTheme(R.style.Theme_FLO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs()
        inputDummyAlbums()
        initPlayList()
        initBottomNavigation()

        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        binding.btnMiniplayerNext.setOnClickListener {
            moveSong(1)
            playSong()
        }
        binding.btnMiniplayerPrevious.setOnClickListener {
            moveSong(-1)
            playSong()
        }
        binding.mainMiniplayerBtn.setOnClickListener {
            playSong()
        }
        initData()
    }

    private fun initData() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        val songSec = spf.getInt("songSec", 0)

        songs.addAll(songDB.songDao().getSongs())
        albums.addAll(songDB.albumDao().getAlbums())

        song = if (songId == 0) {
            songDB.songDao().getSong(1)
        } else {
            songDB.songDao().getSong(songId)
        }
        song.second = songSec
        resetMedia()
        mediaPlayer?.seekTo(song.second * 1000)
        setMiniPlayer(song)

    }

    private fun moveSong(direct: Int) {
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }
        nowPos += direct
        setMiniPlayerBtn()
    }

    override fun onPause() {
        super.onPause()
        binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplayer_play)
        saveCurrentSongState()
    }

    private fun saveCurrentSongState() {
        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", song.id)
        editor.putInt("songSec", song.second)
        editor.putBoolean("isPlaying", song.isPlaying)
        editor.apply()
    }

    private fun setMiniPlayerBtn() {
        song = songs[nowPos]
        song.second = 0
        timer?.interrupt()
        startTimer()
        resetMedia()
        binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplayer_play)
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainMiniplayerProgressSb.progress = 0
    }

    private fun startTimer() {
        timer?.interrupt()
        timer = Timer(song.playTime, song.second, song.isPlaying)
        timer?.start()
    }

    private fun playSong() {
        if (!song.isPlaying) {
            binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplay_pause)
            song.isPlaying = true
            mediaPlayer?.start()
            timer?.isPlaying = true
        } else {
            binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplayer_play)
            song.isPlaying = false
            mediaPlayer?.pause()
            timer?.isPlaying = false
        }
    }

    private fun resetMedia() {
        mediaPlayer?.reset()
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        if (music != 0) { // 유효한 리소스 ID인지 확인
            mediaPlayer = MediaPlayer.create(this, music)
            mediaPlayer?.seekTo(song.second * 1000)
        } else {
            Log.e("MainActivity", "Invalid resource ID for song: ${song.music}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.interrupt()
        mediaPlayer?.pause()
        saveCurrentSongState()
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setMiniPlayer(song: Song){
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainMiniplayerProgressSb.progress = (song.second * 100000) / song.playTime
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)
        val songSec = sharedPreferences.getInt("songSec", 0)
        nowPos = getPlayingSongPosition(songId)
        song = songs[nowPos]
        song.second = songSec
        resetMedia()
        mediaPlayer?.seekTo(song.second * 1000)
        setMiniPlayer(song)
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0
    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    // Week 7 Lecture 47
    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "butter",
                R.drawable.img_album_exp,
                false,
                1
            )
        )
        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                2
            )
        )
        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                230,
                false,
                "next_level",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )
        songDB.songDao().insert(
            Song(
                "Boy With Luv",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "boy_with_luv",
                R.drawable.img_album_exp4,
                false,
                4
            )
        )
        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                230,
                false,
                "bbom_bboom",
                R.drawable.img_album_exp5,
                false,
                5
            )
        )
        songDB.songDao().insert(
            Song(
                "Weekend",
                "태연 (Tae Yeon)",
                0,
                230,
                false,
                "weekend",
                R.drawable.img_album_exp6,
                false,
                6
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun inputDummyAlbums() {
        songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.albumDao().getAlbums()

        if (songs.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                2,
                "IU 5th Album 'LILAC'",
                "아이유 (IU)", R.drawable.img_album_exp2
            )
        )
        songDB.albumDao().insert(
            Album(
                1,
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp,
            )
        )
        songDB.albumDao().insert(
            Album(
                3,
                "iScreaM Vol.10 : Next Level Remixes",
                "에스파 (aespa)",
                R.drawable.img_album_exp3,
            )
        )
        songDB.albumDao().insert(
            Album(
                4,
                "MAP OF THE SOUL : PERSONA",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp4,
            )
        )
        songDB.albumDao().insert(
            Album(
                5,
                "GREAT!",
                "모모랜드 (MomoLand)",
                R.drawable.img_album_exp5,
            )
        )
        songDB.albumDao().insert(
            Album(
                6,
                "Weekend",
                "태연 (TaeYeon)",
                R.drawable.img_album_exp6,
            )
        )
        val _albums = songDB.albumDao().getAlbums()
        Log.d("album data", _albums.toString())
    }

    inner class Timer(private val playTime: Int, private var second: Int, var isPlaying: Boolean = true) : Thread() {
        private var mills: Float = (second * 1000).toFloat()

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (song.second >= playTime) {
                        song.second = 0
                        mills = 0f
                        resetMedia()
                        runOnUiThread {
                            playSong()
                        }
                    }
                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.mainMiniplayerProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }
                        if (mills % 1000 == 0f) {
                            song.second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다 ${e.message}")
            }
        }
    }
}