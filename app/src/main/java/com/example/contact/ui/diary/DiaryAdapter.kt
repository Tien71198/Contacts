package com.example.contact.ui.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contact.R
import com.example.contact.model.Logs
import kotlinx.android.synthetic.main.adapter_calllogs.view.*

class DiaryAdapter(
        private val mList: List<Logs>
) : RecyclerView.Adapter<DiaryAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.adapter_calllogs, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(logs: Logs) {
            itemView.apply {
                mTextViewNumber.text = logs.number
                mTextViewDuration.text = logs.duration + "s"
                mTextViewCallStart.text = logs.date
            }
        }
    }
}
