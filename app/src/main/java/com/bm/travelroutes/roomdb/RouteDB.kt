package com.bm.travelroutes.roomdb

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.bm.travelroutes.model.Places


 @Database(entities = [Places::class], version = 1)
  abstract class RouteDB : RoomDatabase() {
  abstract fun placeDao(): com.bm.travelroutes.roomdb.Dao
 }

