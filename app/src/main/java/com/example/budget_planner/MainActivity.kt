package com.example.budget_planner

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.budget_planner.databinding.ActivityMainBinding
import com.example.budget_planner.databinding.FirstDataDialogLayoutBinding



class MainActivity : AppCompatActivity() {


   private lateinit var binding: ActivityMainBinding
   private lateinit var bindingFirstDataDialog:FirstDataDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = getSharedPreferences("userFirstData", Context.MODE_PRIVATE)
        checkWhetherFirstUseApp(sharedPref,binding)

    }

    private fun checkWhetherFirstUseApp(sharedPref: SharedPreferences,binding: ActivityMainBinding)
    {
        if(!sharedPref.contains("name"))
        {
            showFirstDataDialog(sharedPref,binding)
        }
        else
        {
            showFirstUserData(sharedPref,binding)
        }
    }



    private fun showFirstDataDialog(sharedPref: SharedPreferences,binding: ActivityMainBinding)
    {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.first_data_dialog_layout,null)
        bindingFirstDataDialog= FirstDataDialogLayoutBinding.bind(dialogLayout)
        val etName=bindingFirstDataDialog.etName
        val etIncome=bindingFirstDataDialog.etIncome
        val etSavings=bindingFirstDataDialog.etSavings

        with(builder) {
            setTitle(getString(R.string.firstTitleDialog))
            setPositiveButton(getString(R.string.ok)) { _,_ ->

             val checkError=serviceDialogEnterErrors(etName,etIncome,etSavings)
                if(!checkError){
                    val editor: SharedPreferences.Editor = sharedPref.edit()
                    editor.putString("name",etName.text.toString())
                    editor.putString("income",etIncome.text.toString())
                    editor.putString("savings",etSavings.text.toString())
                    editor.apply()
                    showFirstUserData(sharedPref,binding)
                }
                else{
                    showFirstDataDialog(sharedPref,binding)
                    return@setPositiveButton

                }
            }
            setView(dialogLayout)
            show()

        }
    }
    private fun showFirstUserData(sharedPref: SharedPreferences,binding: ActivityMainBinding)
    {
        binding.tvName.text= sharedPref.getString("name","")
        binding.tvIncome.text= sharedPref.getString("income","")
        binding.tvSavings.text= sharedPref.getString("savings","")


    }
    private fun serviceDialogEnterErrors(etName: EditText,etIncome: EditText,etSavings: EditText): Boolean
    {
        var checkError = false

        val name=etName.text.trim().toString()
        val income= etIncome.text.toString()
        val savings= etSavings.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(applicationContext,getString(R.string.errorEmptyName),Toast.LENGTH_LONG).show()

            checkError= true
        }
        else if (income.isEmpty()) {
            Toast.makeText(applicationContext,getString(R.string.errorEmptyIncome),Toast.LENGTH_LONG).show()
            checkError= true
        }
        else if (income.toDouble()==0.0) {
            Toast.makeText(applicationContext,getString(R.string.errorIncomeEqualZero),Toast.LENGTH_LONG).show()
            checkError= true
        }
        else if (savings.isEmpty()) {
            Toast.makeText(applicationContext,getString(R.string.errorEmptySavings),Toast.LENGTH_LONG).show()
            checkError= true
        }
        else if (savings.toDouble()==0.0) {
            Toast.makeText(applicationContext,getString(R.string.errorSavingsEqualZero),Toast.LENGTH_LONG).show()
            checkError= true
        }
        else if (savings.toDouble()>=income.toDouble())    // An income HAVE TO be greater than savings because in the future we need to add costs to monthly budget
         {
            Toast.makeText(applicationContext,getString(R.string.toastSavingsLessThanIncome),Toast.LENGTH_LONG).show()
            checkError= true
        }
        return checkError

    }
}