package com.openfire.xmppchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.Occupant

class GroupUserAdapter(
    private val data: List<Affiliate>
) :
    RecyclerView.Adapter<GroupUserAdapter.MyViewHolder>() {
    private var listener: GroupClickListener? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tvName)
        var role: TextView = view.findViewById(R.id.tvRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_group_user, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val occupant = data[position]
        holder.name.text = occupant.nick
        holder.role.text = occupant.affiliation!!.name
        /* holder.itemView.setOnClickListener {
             listener?.onGroupClick(groupInfo)
         }*/
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
