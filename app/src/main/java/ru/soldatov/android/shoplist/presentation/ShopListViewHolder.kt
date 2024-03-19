package ru.soldatov.android.shoplist.presentation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.soldatov.android.shoplist.R

class ShopListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val texViewName: TextView = view.findViewById(R.id.tv_name)
    val textViewCount: TextView = view.findViewById(R.id.tv_count)
}