package com.example.flo

import android.content.Context
import androidx.room.*

@Database(entities = [Song::class,Album::class], version = 1)
abstract class SongDatabase:RoomDatabase() {
    abstract fun songDao():SongDao
    abstract fun albumDao():AlbumDao

    companion object{
        private var instance:SongDatabase?=null

        @Synchronized
        fun getInstance(context: Context):SongDatabase?{
            if(instance==null){
                synchronized(SongDatabase::class){
                    instance=Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song-database" // 다른 데이터 베이스와 이름이 겹치면 꼬임
                    ).allowMainThreadQueries().build()  // 편의상 메인 쓰레드에서 처리
                }
            }
            return instance
        }
    }
}