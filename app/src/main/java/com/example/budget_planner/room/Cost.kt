package com.example.budget_planner.room

import androidx.room.Entity
import androidx.room.PrimaryKey
// model class useful to Room
@Entity(tableName = "cost_table")
data class Cost(
           var name:String,
           var category:String,
           var period:String,
           var amount:Float,
           var date: String, // if date is "type :Date" - error (?)
           ){



    @PrimaryKey(autoGenerate = true)
    var id:Int=0

}
// below data class is useful for take "sum" from database through SQL commands:
// @Query("SELECT SUM(amount) AS sum FROM cost_table  WHERE category = (:category) AND date= (:date)")
// @Query("SELECT SUM(amount) AS sum FROM cost_table  WHERE date= (:date)")
data class SumCost(var sum:Float)

