package com.example.flo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment(), BottomSheetFragment.BottomSheetListener {

    lateinit var binding: FragmentLockerBinding
    private val information = arrayListOf("저장한 곡","음악 파일", "저장 앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp){
                tab, position ->
            tab.text = information[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener{
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initViews()
        initRecylcerView()
    }

    private fun getJwt(): Int{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt", 0)
    }

    private fun initViews(){
        val jwt : Int = getJwt()
        if (jwt == 0){
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener{
                startActivity(Intent(activity,LoginActivity::class.java))
            }
        }else{
            binding.lockerLoginTv.text = "로그아웃"
            binding.lockerLoginTv.setOnClickListener{
                // 로그아웃 진행
                logout()
                startActivity(Intent(activity,LoginActivity::class.java))
            }
        }
    }

    private fun logout(){
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()
        editor.remove("jwt")
        editor.apply()
    }

    var isSelect: Boolean = false
    private fun initRecylcerView(){
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