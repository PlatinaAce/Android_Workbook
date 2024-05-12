package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timer
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment(), CommunicationInterface {

    lateinit var binding: FragmentHomeBinding
    lateinit var indicator : CircleIndicator3

    //Week 6 Lecture 37
    private var albumDatas = ArrayList<Album>()

//  일정 시간 후 다음 화면으로 넘어가는 자동 슬라이드 구현해보기
    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())

    override fun sendData(album: Album) {
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.updateMainPlayerCl(album)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 데이터 리스트 생성 더미 데이터
        albumDatas.apply {
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp, arrayListOf(
                Song("Butter", "방탄소년단 (BTS)", 0, 200, false, "music_butter", R.drawable.img_album_exp),
                Song("Butter (Hotter Remix)", "방탄소년단 (BTS)", 0, 200, false, "music_butter", R.drawable.img_album_exp),
                Song("Butter (Sweeter Remix)", "방탄소년단 (BTS)", 0, 200, false, "music_butter", R.drawable.img_album_exp),
                Song("Butter (Cooler Remix)", "방탄소년단 (BTS)", 0, 200, false, "music_butter", R.drawable.img_album_exp)
            )))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2, arrayListOf(
                Song("Lilac", "아이유 (IU)", 0, 200, false, "music_lilac", R.drawable.img_album_exp2),
                Song("Flu", "아이유 (IU)", 0, 200, false, "music_lilac", R.drawable.img_album_exp2),
                Song("Coin", "아이유 (IU)", 0, 200, false, "music_lilac", R.drawable.img_album_exp2),
                Song("봄 안녕 봄", "아이유 (IU)", 0, 200, false, "music_lilac", R.drawable.img_album_exp2)
            )))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3, arrayListOf(
                Song("Next Level", "에스파 (AESPA)", 0, 200, false, "music_nextlevel", R.drawable.img_album_exp3)
            )))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4, arrayListOf(
                Song("Boy With Luv", "방탄소년단 (BTS)", 0, 200, false, "music_boywithluv", R.drawable.img_album_exp4)
            )))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5, arrayListOf(
                Song("BBoom BBoom", "모모랜드 (MOMOLAND)", 0, 200, false, "music_bboombboom", R.drawable.img_album_exp5)
            )))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6, arrayListOf(
                Song("Weekend", "태연 (Tae Yeon)", 0, 200, false, "music_weekend", R.drawable.img_album_exp6)
            )))
        }

        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object: AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album){
                changeAlbumFragment(album)
            }

            override fun onPlayAlbum(album: Album){
                sendData(album)
            }
        })


        // Week 3 Lecture 16
        val homePannelAdapter = HomePannelVPAdapter(this)
        binding.homePannelBackgroundVp.adapter = homePannelAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // 오픈소스 라이브러리인 CircleIndicator 활용해서 슬라이드 만들어보기
        binding.homePannelBackgroundCi.setViewPager(binding.homePannelBackgroundVp)


        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        startAutoSlide(homePannelAdapter)

        return binding.root

    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    //  일정 시간 후 다음 화면으로 넘어가는 자동 슬라이드 구현해보기
    private fun startAutoSlide(adapter: HomePannelVPAdapter){
        timer.scheduleAtFixedRate(5000,5000){
            handler.post{
                val nextItem = binding.homePannelBackgroundVp.currentItem + 1
                if (nextItem < adapter.itemCount) {
                    binding.homePannelBackgroundVp.currentItem = nextItem
                }
                else{
                    binding.homePannelBackgroundVp.currentItem = 0
                }
            }
        }
    }
}