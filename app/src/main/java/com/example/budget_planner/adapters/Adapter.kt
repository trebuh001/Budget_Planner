package com.example.budget_planner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_planner.databinding.LayoutItemBinding
import com.example.budget_planner.room.Cost

class Adapter(private val listOfCosts: List<Cost>,
              private val listener: OnItemClickListener
): RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutItem = LayoutItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(layoutItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.tvName.text = listOfCosts[position].name
            holder.binding.tvCategory.text = listOfCosts[position].category
            holder.binding.tvPeriod.text = listOfCosts[position].period
            holder.binding.tvAmount.text = listOfCosts[position].amount.toString()


    }
    override fun getItemCount(): Int {
        return listOfCosts.size
    }
    inner class ViewHolder(val binding: LayoutItemBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position =  adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(listOfCosts[position])
            }

        }
    }
    interface OnItemClickListener {
        fun onItemClick(listOfCosts: Cost)
    }
}




