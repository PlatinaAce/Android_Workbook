package com.example.flo

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemLockerAlbumBinding

class LockerAlbumRVAdapter (private val albumList: ArrayList<Album>): RecyclerView.Adapter<LockerAlbumRVAdapter.ViewHolder>(){

    private val changeBtnStatus = SparseBooleanArray()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LockerAlbumRVAdapter.ViewHolder {
        val binding: ItemLockerAlbumBinding = ItemLockerAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockerAlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
        // Week 6 Lecture 40
        holder.itemView.setOnClickListener{ mItemClickListener.onItemClick(albumList[position]) }

        // ...버튼을 클릭하면 앨범이 사라지는 기능
        holder.binding.itemLockerAlbumMoreIv.setOnClickListener { mItemClickListener.onRemoveAlbum(position)}
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemLockerAlbumBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(album: Album){
            binding.itemLockerAlbumTitleTv.text = album.title
            binding.itemLockerAlbumSingerTv.text = album.singer
            binding.itemLockerAlbumCoverImgIv.setImageResource(album.coverImg!!)

            // 재생 버튼 클릭 시 이미지 정지 버튼으로 바뀌는 기능
            if(changeBtnStatus.get(position, false)){
                binding.itemLockerAlbumPlayImgIv.visibility = View.GONE
                binding.itemLockerAlbumPauseImgIv.visibility = View.VISIBLE
            }
            else{
                binding.itemLockerAlbumPlayImgIv.visibility = View.VISIBLE
                binding.itemLockerAlbumPauseImgIv.visibility = View.GONE
            }

            binding.itemLockerAlbumPlayImgIv.setOnClickListener {
                changeBtnStatus.put(position,true)
                notifyDataSetChanged()
            }
            binding.itemLockerAlbumPauseImgIv.setOnClickListener {
                changeBtnStatus.put(position,false)
                notifyDataSetChanged()
            }

        }
    }

    interface MyItemClickListener{
        fun onItemClick(album: Album)

        // Album 제목을 클릭하면 앨범이 사라지는 기능
        fun onRemoveAlbum(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    fun addItem(album: Album){
        albumList.add(album)
        notifyDataSetChanged()
    }

    // Album 제목을 클릭하면 앨범이 사라지는 기능
    fun removeItem(position: Int){
        albumList.removeAt(position)
        changeBtnStatus.put(position,false)
        notifyDataSetChanged()
    }
}