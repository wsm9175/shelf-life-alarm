package com.wsm9175.shelf_life.view.adapter

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wsm9175.shelf_life.R
import com.wsm9175.shelf_life.db.entity.FoodEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodListRVAdapter(val context: Context, val dataSet: List<FoodEntity>) :
    RecyclerView.Adapter<FoodListRVAdapter.ViewHolder>() {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val nowDate: LocalDate = LocalDate.now()

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodImage = view.findViewById<ImageView>(R.id.foodImage)
        val nameTextView = view.findViewById<TextView>(R.id.name)
        val dateTextView = view.findViewById<TextView>(R.id.date)
        val noteTextView = view.findViewById<TextView>(R.id.note)
        val warningImage = view.findViewById<ImageView>(R.id.warning)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.onClick(holder.itemView, position)
        }

        if (dataSet[position].image != null) holder.foodImage.setImageBitmap(dataSet[position].image)
        holder.nameTextView.text = dataSet[position].name
        val startDate = dataSet[position].buyDate.format(formatter)
        val endDate = dataSet[position].shelfLife.format(formatter)
        val date = "${startDate} ~ ${endDate}"
        holder.dateTextView.text = date
        holder.noteTextView.text = dataSet[position].note
        // 날짜에 따른 경고 이미지 출력
        Log.d("rv", nowDate.compareTo(dataSet[position].shelfLife).toString())
        if (nowDate.compareTo(dataSet[position].shelfLife) < 0)
            holder.warningImage.setImageBitmap(null)
    }
}