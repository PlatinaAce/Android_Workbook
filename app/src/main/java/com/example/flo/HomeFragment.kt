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
    private lateinit var songDB: SongDatabase

    override fun sendData(album: Album) {
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.updateMainPlayerCl(album.id)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 데이터 리스트 생성 더미 데이터
        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())

        // 더미데이터랑 Adapter 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)

        // 리사이클러뷰에 어댑터를 연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object: AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album){
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
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