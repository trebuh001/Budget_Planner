package com.example.budget_planner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.budget_planner.room.Cost
import com.example.budget_planner.room.CostRepository
import com.example.budget_planner.room.SumCost
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
// ViewModel class (pattern MVVM) - "get" methods from CostRepository
// and create public methods for Activity
class CostViewModel(application: Application): AndroidViewModel(application) {

    private var costRepository: CostRepository =
        CostRepository(application)
    private fun allCost(date: String): Deferred<LiveData<List<Cost>>> =
        costRepository.getAllCostsAsync(date)
    private fun allSumByCategory(category: String,date: String): Deferred<LiveData<List<SumCost>>> =
        costRepository.getSumByCategoryAsync(category,date)
    private fun allSum(date: String): Deferred<LiveData<List<SumCost>>> =
        costRepository.getSumAsync(date)
    fun insertCost(cost: Cost) {
        costRepository.insertCost(cost)
    }
    fun deleteCost(cost: Cost) {
        costRepository.deleteCost(cost)
    }
    fun getAllCost(date: String) : LiveData<List<Cost>> = runBlocking {
        allCost(date).await()
    }
    fun getAllSumByCategory(category: String,date: String) : LiveData<List<SumCost>> = runBlocking{
       allSumByCategory(category,date).await()
    }
    fun getSum(date: String) : LiveData<List<SumCost>> = runBlocking{
        allSum(date).await()
    }
}