package com.example.budget_planner.room

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
// below class "takes" methods from interface CostDao and "send" to class CostViewModel
// (MVVM pattern)
class CostRepository(application: Application) {
    private var costDao: CostDao

    init {
        val database = CostDatabase.getInstance(application.applicationContext)
        costDao = database!!.costDao()
    }

    fun insertCost(cost: Cost) :Job=
        CoroutineScope(Dispatchers.IO).launch{
        costDao.insert(cost)
    }

    fun deleteCost(cost:Cost):Job =
        CoroutineScope(Dispatchers.IO).launch {
            costDao.delete(cost)
        }
    fun getAllCostsAsync(date: String): Deferred<LiveData<List<Cost>>> =
        CoroutineScope(Dispatchers.IO).async {
            costDao.getAllCosts(date)
        }
    fun getSumByCategoryAsync(category: String,date: String): Deferred<LiveData<List<SumCost>>> =
        CoroutineScope(Dispatchers.IO).async {
            costDao.getSumByCategory(category,date)
        }
    fun getSumAsync(date: String): Deferred<LiveData<List<SumCost>>> =
        CoroutineScope(Dispatchers.IO).async {
            costDao.getSum(date)
        }

}