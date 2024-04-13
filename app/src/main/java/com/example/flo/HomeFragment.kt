package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timer
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var indicator : CircleIndicator3

//  일정 시간 후 다음 화면으로 넘어가는 자동 슬라이드 구현해보기
    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homeTodayMusicAlbumImg01Iv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm,AlbumFragment()).commitAllowingStateLoss()
        }

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