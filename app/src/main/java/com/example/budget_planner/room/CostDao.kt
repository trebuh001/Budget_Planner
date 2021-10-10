package com.example.budget_planner.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CostDao {
    @Insert
    fun insert(cost: Cost)
    @Delete
    fun delete(cost: Cost)

    @Query("SELECT * FROM cost_table WHERE date = (:date)")
    fun getAllCosts(date: String): LiveData<List<Cost>>


  @Query("SELECT SUM(amount) AS sum FROM cost_table  WHERE category = (:category) AND date= (:date)")
   fun getSumByCategory(category: String,date: String): LiveData<List<SumCost>>
    @Query("SELECT SUM(amount) AS sum FROM cost_table  WHERE date= (:date)")
    fun getSum(date: String): LiveData<List<SumCost>>
}