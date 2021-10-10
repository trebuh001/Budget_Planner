package com.example.budget_planner.room

import androidx.room.Entity
import androidx.room.PrimaryKey

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
data class SumCost(var sum: Float)

