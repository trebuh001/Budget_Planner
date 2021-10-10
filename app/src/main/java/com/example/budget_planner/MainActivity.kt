package com.example.budget_planner

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import android.graphics.Color
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import com.example.budget_planner.databinding.ActivityMainBinding
import com.example.budget_planner.databinding.FirstDataDialogLayoutBinding
import android.view.MenuItem
import android.widget.*

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budget_planner.adapters.Adapter
import com.example.budget_planner.databinding.AddCostDataDialogLayoutBinding
import com.example.budget_planner.objects.Category
import com.example.budget_planner.room.Cost
import com.example.budget_planner.room.SumCost
import com.example.budget_planner.viewmodel.CostViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), Adapter.OnItemClickListener {

    private lateinit var viewModel: CostViewModel  // MVVM pattern
    private lateinit var binding: ActivityMainBinding
    // 2 variables below: using ViewBinding for take id of objects from layouts - save way.
    // Reason: Older option: "kotlinx.synthetic" is deprecated
    private lateinit var bindingFirstDataDialog: FirstDataDialogLayoutBinding
    private lateinit var bindingAddCostDataDialogLayoutBinding: AddCostDataDialogLayoutBinding
    private var adapter: Adapter? = null //nullable type with using "?"
    private lateinit var listOfCosts: LiveData<List<Cost>>
    private lateinit var sumOfCosts: LiveData<List<SumCost>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val sdf = SimpleDateFormat("MM/yyyy")
        val selectedDate: String = sdf.format(Date())

        binding.tvCurrentDate.text = selectedDate
        setContentView(binding.root)
        binding.btnPrevious.setOnClickListener {
            decrementDate()
            handleRecyclerView(binding)
        }
        binding.btnNext.setOnClickListener {
            incrementDate()
            handleRecyclerView(binding)
        }
        viewModelProvider()
        val sharedPref = getSharedPreferences("userFirstData", Context.MODE_PRIVATE)
        checkWhetherFirstUseApp(sharedPref, binding)
        //handleRecyclerView(binding)

    }

    // result of below function is distinction between user: income and savings
    private fun resultDistinctIncomeSavings(): Float {
        return binding.tvIncome.text.toString().toFloat() - binding.tvSavings.text.toString()
            .toFloat()
    }
    // below function get SUM of amount cost in Room Database by interface (file: CostDao)
    // In case of this function: use clause "WHERE" date parameters
    private fun getUsedBudget() {
        var resultUnusedBudget: Float? = null
        var resultUnusedBudgetToString: String?
        var categorySum2: Float?
        var tvUnusedBudget: TextView?
        val resultDistinct = resultDistinctIncomeSavings()
        val date = binding.tvCurrentDate.text.toString()

        sumOfCosts = viewModel.getSum(date)
        sumOfCosts.observe(this, {
            if (it.isNotEmpty()) {
                categorySum2 = it[0].sum
                tvUnusedBudget = binding.tvUnusedBudgetGenerally

                if (categorySum2 == 0.0F) {
                    resultUnusedBudget = 100.0F
                    tvUnusedBudget!!.setTextColor(getColor(R.color.green_percent))
                } else if (resultDistinct == categorySum2) {
                    resultUnusedBudget = 0.0F

                } else if (resultDistinct < categorySum2!!) {
                    resultUnusedBudget = 100 - (categorySum2!! / resultDistinct * 100)
                    tvUnusedBudget!!.setTextColor(Color.RED)
                } else if (resultDistinct > categorySum2!!) {
                    resultUnusedBudget = 100 - (categorySum2!! / resultDistinct * 100)

                    tvUnusedBudget!!.setTextColor(getColor(R.color.green_percent))
                }
                resultUnusedBudgetToString= String.format("%.2f",resultUnusedBudget!!)
                resultUnusedBudgetToString+="%"
//
                tvUnusedBudget!!.text = resultUnusedBudgetToString
                getUsedBudgetByCategory(categorySum2!!)

            }
            else
            {
                binding.tvUnusedBudgetGenerally.text ="0.0%"
                binding.tvUnusedBudgetCharges.text ="0.0%"
                binding.tvUnusedBudgetShopping.text ="0.0%"
                binding.tvUnusedBudgetPleasures.text ="0.0%"
                binding.tvUnusedBudgetHobby.text ="0.0%"
            }
        })
    }
    // below function get SUM of amount cost in Room Database by interface (file: CostDao)
    // In case of this function: use clause "WHERE"  by category and date parameters
    private fun getUsedBudgetByCategory(resultUnusedBudget: Float) {
        var resultUnusedBudgetByCategory: Float?
        var resultUnusedBudgetByCategoryToString: String?
        var categorySum2: Float?
        var tvUnusedBudget: TextView? = null
        val category = Category
        val date = binding.tvCurrentDate.text.toString()
        val categorySum = arrayListOf(0.0F, 0.0F, 0.0F, 0.0F)
        for (i in 0..3) {
            sumOfCosts = viewModel.getAllSumByCategory(category.categoryPl[i], date)
            sumOfCosts.observe(this, {
                if (it.isNotEmpty()) {
                    categorySum[i] = it[0].sum
                    if (i == 0) {
                        tvUnusedBudget = binding.tvUnusedBudgetCharges
                    } else if (i == 1) {
                        tvUnusedBudget = binding.tvUnusedBudgetShopping
                    } else if (i == 2) {
                        tvUnusedBudget = binding.tvUnusedBudgetPleasures
                    } else if (i == 3) {
                        tvUnusedBudget = binding.tvUnusedBudgetHobby
                    }

                    categorySum2 = categorySum[i]
                    if (categorySum2 == 0.0F)
                        resultUnusedBudgetByCategory = 0.0F
                    else if (resultUnusedBudget == categorySum2)
                        resultUnusedBudgetByCategory = 100.0F
                    else
                        resultUnusedBudgetByCategory = (categorySum2!! / resultUnusedBudget * 100)

                    resultUnusedBudgetByCategoryToString= String.format("%.2f",resultUnusedBudgetByCategory)
                    resultUnusedBudgetByCategoryToString+="%"
                    tvUnusedBudget!!.text = resultUnusedBudgetByCategoryToString
                }
            })
        }
    }
    // below function is idea for create simple calendar logic from scratch
    // this function handle button "Previous month"
    private fun decrementDate() {
        val currentDate = binding.tvCurrentDate.text.toString()
        var month1 = currentDate.subSequence(0, 1).toString().toInt()
        var month2 = currentDate.subSequence(1, 2).toString().toInt()
        var year = currentDate.subSequence(3, 7).toString().toInt()
        if (month1 == 0) {
            if (month2 > 1) {
                month2--
            } else if (year > 1990) {
                year--
                month1 = 1
                month2 = 2
            }
        } else if (month1 == 1) {
            if (month2 > 0) {
                month2--
            } else {
                month1 = 0
                month2 = 9
            }
        }
        val monthString1 = month1.toString()
        val monthString2 = month2.toString()
        val yearString = year.toString()

        val newDate = "$monthString1$monthString2/$yearString"
        binding.tvCurrentDate.text = newDate
    }
    // below function is idea for create simple calendar logic from scratch
    // this function handle button "Next month"
    private fun incrementDate() {
        val currentDate = binding.tvCurrentDate.text.toString()
        var month1 = currentDate.subSequence(0, 1).toString().toInt()
        var month2 = currentDate.subSequence(1, 2).toString().toInt()
        var year = currentDate.subSequence(3, 7).toString().toInt()
        if (month1 == 0) {
            if (month2 == 9) {
                month1 = 1
                month2 = 0
            } else {
                month2++
            }
        } else if (month1 == 1) {
            if (month2 < 2) {
                month2++
            } else if (year <= 2100) {
                year++
                month1 = 0
                month2 = 1
            }
        }
        val monthString1 = month1.toString()
        val monthString2 = month2.toString()
        val yearString = year.toString()
        val newDate = "$monthString1$monthString2/$yearString"
        binding.tvCurrentDate.text = newDate
    }
    // below function defines object of new model class "CostViewModel",
    //for asynchronous operations (MVVM pattern)
    private fun viewModelProvider() {
        viewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(CostViewModel::class.java)
    }
    //below function handle "tap on list" (RecyclerView) (for deleting cost operation).
    // If after deleting cost database will empty for current month,
    //user will see "add new cost dialog"
    override fun onItemClick(listOfCosts: Cost) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.titleWarningDeleteCost))
        builder.setMessage(R.string.msgWarningDeleteCost)
        builder.setIcon(R.drawable.warning)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            viewModel.deleteCost(listOfCosts)
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
        }
        builder.show()
        this.listOfCosts.observe(this, {
            if (it.isNotEmpty()) {
                adapter = Adapter(it, this)
                binding.rvCostsList.adapter = adapter
            } else {
                binding.rvCostsList.adapter = null
                val toast = Toast.makeText (
                        applicationContext,
                    getString(R.string.toastEmptyMonth),
                    Toast.LENGTH_SHORT)
                toast.show()
                addNewCost()
            }
        })
    }
    //below function check if user set first data yet
    private fun checkWhetherFirstUseApp(
        sharedPref: SharedPreferences,
        binding: ActivityMainBinding
    ) {
        if (!sharedPref.contains("name")) {
            showFirstDataDialog(sharedPref, binding)
        } else {
            showFirstUserData(sharedPref, binding)
            handleRecyclerView(binding)
        }
    }
    //below function is used for set data from room database to
    //RecyclerView by ViewModel class (MVVM pattern) and adapter
    private fun handleRecyclerView(binding: ActivityMainBinding) {
        binding.rvCostsList.layoutManager = LinearLayoutManager(this)
        val date = binding.tvCurrentDate.text.toString()
        listOfCosts = viewModel.getAllCost(date)
        listOfCosts.observe(this, {
            if (it.isNotEmpty()) {
                adapter = Adapter(it, this)
                binding.rvCostsList.adapter = adapter
                getUsedBudget()
            } else {
                binding.rvCostsList.adapter = null
                val toast =Toast.makeText(
                    applicationContext,
                    getString(R.string.toastEmptyMonth),
                    Toast.LENGTH_SHORT)
                toast.show()
                addNewCost()
            }
        })
    }
    //below function show first dialog while user first time opens the app
    private fun showFirstDataDialog(sharedPref: SharedPreferences, binding: ActivityMainBinding) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.first_data_dialog_layout, null)
        bindingFirstDataDialog = FirstDataDialogLayoutBinding.bind(dialogLayout)
        val etName = bindingFirstDataDialog.etName
        val etIncome = bindingFirstDataDialog.etIncome
        val etSavings = bindingFirstDataDialog.etSavings

        with(builder) {
            setTitle(getString(R.string.firstTitleDialog))
            setPositiveButton(getString(R.string.ok)) { _, _ ->

                val checkError = serviceEnterDialogErrors(etName, etIncome, etSavings)
                if (!checkError) {
                    val editor: SharedPreferences.Editor = sharedPref.edit()

                    var incomeRound = etIncome.text.toString().toFloat()
                    incomeRound=incomeRound.roundToDecimals(2)
                    val incomeString= incomeRound.toString()

                    var savingsRound = etSavings.text.toString().toFloat()
                    savingsRound = savingsRound.roundToDecimals(2)
                    val savingsString = savingsRound.toString()
                    editor.putString("name", etName.text.toString())
                    editor.putString("income", incomeString)
                    editor.putString("savings", savingsString)
                    editor.apply()
                    showFirstUserData(sharedPref, binding)
                } else {
                    showFirstDataDialog(sharedPref, binding)
                    return@setPositiveButton
                }
            }
            setView(dialogLayout)
            show()
        }
    }
    //below function show "first user data" from SharedPreferences class
    private fun showFirstUserData(sharedPref: SharedPreferences, binding: ActivityMainBinding) {
        binding.tvName.text = sharedPref.getString("name", "")
        binding.tvIncome.text = sharedPref.getString("income", "")
        binding.tvSavings.text = sharedPref.getString("savings", "")
    }
    //below function check whether user set right data while first open of app
    private fun serviceEnterDialogErrors(
        etName: EditText,
        etIncome: EditText,
        etSavings: EditText
    ): Boolean {
        var checkError = false
        val name = etName.text.trim().toString()
        val income = etIncome.text.toString()
        val savings = etSavings.text.toString()
        when {
            name.isEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toastEmptyName),
                    Toast.LENGTH_SHORT
                ).show()
                checkError = true
            }
            income.isEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toastEmptyIncome),
                    Toast.LENGTH_SHORT
                ).show()
                checkError = true
            }
            income.toFloat() == 0.0F -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toastIncomeEqualZero),
                    Toast.LENGTH_SHORT
                ).show()
                checkError = true
            }
            savings.isEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toastEmptySavings),
                    Toast.LENGTH_SHORT
                ).show()
                checkError = true
            }
            savings.toFloat() == 0.0F -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toastSavingsEqualZero),
                    Toast.LENGTH_SHORT
                ).show()
                checkError = true
            }
            savings.toFloat() >= income.toFloat()    // An income HAVE TO be greater than savings because in the future we need to add costs to monthly budget
            -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toastSavingsLessThanIncome),
                    Toast.LENGTH_SHORT
                ).show()
                checkError = true
            }
        }
        return checkError
    }
    //below function check whether user "set empty values" while adding new cost
    private fun serviceEnterDialogErrors(etName: EditText, etAmount: EditText): Boolean {
        var checkError = false
        val name = etName.text.trim().toString()
        val amount = etAmount.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(
                applicationContext,
                getString(R.string.toastEmptyCostName),
                Toast.LENGTH_SHORT
            ).show()
            checkError = true
        } else if (amount.isEmpty()) {
            Toast.makeText(
                applicationContext,
                getString(R.string.toastEmptyCostAmount),
                Toast.LENGTH_SHORT
            ).show()
            checkError = true
        }
        return checkError

    }
    // override function used to "inflate" button from menu_details.xml to ActionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return super.onCreateOptionsMenu(menu)
    }
    // override function below take a button as item from menu_details.xml
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btnAddCost) //if button (as item on ActionBar was tapped)
        {
            addNewCost()
        }
        return super.onOptionsItemSelected(item)
    }
    //below function add new cost using dialog
    private fun addNewCost() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.add_cost_data_dialog_layout, null)
        bindingAddCostDataDialogLayoutBinding = AddCostDataDialogLayoutBinding.bind(dialogLayout)
        bindingAddCostDataDialogLayoutBinding.tvAddedDate.text =
            binding.tvCurrentDate.text.toString()
        val etName = bindingAddCostDataDialogLayoutBinding.etName
        val etAmount = bindingAddCostDataDialogLayoutBinding.etAmount

        with(builder) {
            setTitle(getString(R.string.firstTitleDialog))
            setPositiveButton(getString(R.string.btnAddCost)) { _, _ ->
                val checkError = serviceEnterDialogErrors(etName, etAmount)
                if (!checkError) {
                    val name = bindingAddCostDataDialogLayoutBinding.etName.text.toString()
                    val categoryID =
                        bindingAddCostDataDialogLayoutBinding.rgCategory.checkedRadioButtonId
                    val categoryRB =
                        bindingAddCostDataDialogLayoutBinding.rgCategory.findViewById<RadioButton>(
                            categoryID
                        )
                    val category = categoryRB.text.toString()
                    val periodID =
                        bindingAddCostDataDialogLayoutBinding.rgPeriod.checkedRadioButtonId
                    val periodRB =
                        bindingAddCostDataDialogLayoutBinding.rgPeriod.findViewById<RadioButton>(
                            periodID
                        )
                    val period = periodRB.text.toString()
                    var amount = bindingAddCostDataDialogLayoutBinding.etAmount.text.toString().toFloat()
                    amount=amount.roundToDecimals(2)
                    val date = bindingAddCostDataDialogLayoutBinding.tvAddedDate.text.toString()
                    val cost = Cost(name, category, period, amount, date)
                    viewModel.insertCost(cost)

                } else {
                    finish()
                    startActivity(intent)
                }
            }
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
                finish()
                startActivity(intent)
            }
            setView(dialogLayout)
            show()
        }
    }
    //below function means: refresh activity if back button was pressed
    // (in result going to "current month")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(intent)

    }
}
// below function is for round float (this was need to round to two places)
fun Float.roundToDecimals(decimals: Int): Float {
    var dotAt = 1
    repeat(decimals) { dotAt *= 10 }
    val roundedValue = (this * dotAt).roundToInt()
    return (roundedValue / dotAt) + (roundedValue % dotAt).toFloat() / dotAt
}


