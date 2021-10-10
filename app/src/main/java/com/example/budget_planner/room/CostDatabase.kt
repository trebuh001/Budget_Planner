package com.example.budget_planner.room
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//below class create a instance of database (get model of database from "Cost class model"
// through interface "CostDao")
@Database(entities = [Cost::class],version = 10)
abstract class CostDatabase: RoomDatabase() {
    abstract fun costDao(): CostDao

    companion object{
        private  var instance: CostDatabase? = null

        fun getInstance(context: Context): CostDatabase?
        {
            if(instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    CostDatabase::class.java,
                    "cost_table")
                    .fallbackToDestructiveMigration()
                    .build()
            }
                return instance
        }
    }
}