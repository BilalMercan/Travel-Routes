package com.bm.travelroutes.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bm.travelroutes.model.Places
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.*
import io.reactivex.rxjava3.core.Completable

@Dao
interface Dao {

    @Query("SELECT * FROM Places")
    fun getAll() : Flowable<List<Places>>

    @Insert
    fun insert(places: Places) : Completable

    @Delete
    fun delete(places: Places) : Completable


}