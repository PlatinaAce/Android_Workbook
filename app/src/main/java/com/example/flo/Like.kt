package com.example.flo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LikeTable")
data class Like(
    val userId : Int,
    var albumId : Int
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
