package com.example.flo

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemLockerAlbumBinding
import com.example.flo.databinding.ItemSongBinding

class LockerAlbumRVAdapter (private val albumList: ArrayList<Album>): RecyclerView.Adapter<LockerAlbumRVAdapter.ViewHolder>(){

    private val changeBtnStatus = SparseBooleanArray()

    interface MyItemClickListener{
        fun onItemClick(album: Album)

        // Album 제목을 클릭하면 앨범이 사라지는 기능
        fun onRemoveAlbum(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LockerAlbumRVAdapter.ViewHolder {
        val binding: ItemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockerAlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position], position)
        // Week 6 Lecture 40
        holder.itemView.setOnClickListener{ mItemClickListener.onItemClick(albumList[position]) }

        // ...버튼을 클릭하면 앨범이 사라지는 기능
        holder.binding.itemSongMoreIv.setOnClickListener { mItemClickListener.onRemoveAlbum(position)}
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemSongBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(album: Album, position: Int){
            binding.itemSongSingerTv.text = album.singer
            binding.itemSongTitleTv.text = album.title
            binding.itemSongImgIv.setImageResource(album.coverImg!!)

            if (changeBtnStatus.get(position, false)) {
                binding.itemSongPlayIv.visibility = View.GONE
                binding.itemSongPauseIv.visibility = View.VISIBLE
            } else {
                binding.itemSongPlayIv.visibility = View.VISIBLE
                binding.itemSongPauseIv.visibility = View.GONE
            }
            binding.itemSongPlayIv.setOnClickListener {
                changeBtnStatus.put(position, true)
                notifyDataSetChanged()
            }
            binding.itemSongPauseIv.setOnClickListener {
                changeBtnStatus.put(position, false)
                notifyDataSetChanged()
            }

        }
    }

    fun addItem(album: Album){
        albumList.add(album)
        notifyDataSetChanged()
    }

    // Album 제목을 클릭하면 앨범이 사라지는 기능
    fun removeItem(position: Int){
        albumList.removeAt(position)
        changeBtnStatus.delete(position) // 해당 위치의 상태 삭제
        // 삭제된 이후의 모든 아이템의 상태를 업데이트
        for (i in position until albumList.size) {
            if (changeBtnStatus[i + 1]) {
                changeBtnStatus.put(i, true)
            } else {
                changeBtnStatus.put(i, false)
            }
        }
        notifyDataSetChanged()
    }
}