package com.openfire.xmppchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(
    private val data: List<GroupInfo>
) :
    RecyclerView.Adapter<GroupAdapter.MyViewHolder>() {
    private var listener: GroupClickListener? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_roaster, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val groupInfo = data[position]
        holder.name.text = groupInfo.roomInfo.name
        holder.itemView.setOnClickListener {
            listener?.onGroupClick(groupInfo)
        }
    }

    fun setGroupListener(listener: GroupClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }


    interface GroupClickListener {
        fun onGroupClick(
            entry: GroupInfo

        )
    }
}
