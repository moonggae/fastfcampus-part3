package fastcampus.aop.part3.chapter04.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fastcampus.aop.part3.chapter04.model.Review

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE id == :id")
    fun getOneReview(id : Int) : Review?

    // OnConflictStrategy.REPLACE 속성을 통해 똑같은 id가 들어오면 update 함
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReview(review : Review)
}