package com.openfire.xmppchat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.StandardExtensionElement
import org.jivesoftware.smack.util.StringUtils
import org.jxmpp.jid.BareJid
import org.jxmpp.jid.impl.JidCreate


class ChatAdapter(
    private val data: List<Message>,
    private val currentUser: BareJid,
    private val isGroup: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class PlainTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tvName)
        var msg: TextView = view.findViewById(R.id.tvMessage)
        val main = view.findViewById<ConstraintLayout>(R.id.main)

    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tvName)
        var img: ImageView = view.findViewById(R.id.ivImage)
        val main = view.findViewById<ConstraintLayout>(R.id.main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder =
            if (viewType == ChatType.CHAT.viewType) {
                PlainTextViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_message, parent, false)
                )
            } else if (viewType == ChatType.IMAGE.viewType) {
                ImageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_image, parent, false)
                )
            } else {
                PlainTextViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_message, parent, false)
                )
            }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val msg = data[position]
        val viewType = getItemViewType(position)
        val isSender = currentUser == data[position].to.asBareJid()
        if (viewType == ChatType.CHAT.viewType) {
            if (holder is PlainTextViewHolder) {
                holder.msg.text = msg.body
                setViewGravity(holder.main, holder.msg.id, holder.name.id, isSender)
                holder.msg.gravity = if (isSender) Gravity.START else Gravity.END
                holder.name.gravity = if (isSender) Gravity.START else Gravity.END
                if (isGroup) {
                    holder.name.text = msg.from/*.resourceOrEmpty*/
                    holder.name.visibility = View.VISIBLE
                } else {
                    holder.name.visibility = View.VISIBLE
                }
            }
        } else if (viewType == ChatType.IMAGE.viewType) {
            if (holder is ImageViewHolder) {
                setViewGravity(holder.main, holder.img.id, holder.name.id, isSender)
                GlideApp.with(holder.itemView.context)
                    .load(msg.body)
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(holder.img)

                if (isGroup) {
                    holder.name.text = msg.from/*.resourceOrEmpty*/
                    holder.name.visibility = View.VISIBLE
                } else {
                    holder.name.visibility = View.VISIBLE
                }
            }
        } else {
        }


    }

    fun setViewGravity(
        main: ConstraintLayout,
        viewAlign: Int,
        viewAlign1: Int,
        isSender: Boolean
    ) {
        val set = ConstraintSet()
        set.clone(main)
        if (isSender) {
            set.connect(
                viewAlign,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                0
            )
            set.clear(viewAlign, ConstraintSet.END)

            set.connect(
                viewAlign1,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                0
            )
            set.clear(viewAlign1, ConstraintSet.END)
        } else {
            set.clear(viewAlign, ConstraintSet.START)
            set.connect(
                viewAlign,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                0
            )

            set.clear(viewAlign1, ConstraintSet.START)
            set.connect(
                viewAlign1,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                0
            )
        }
        set.applyTo(main)
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position].subject) {
            ChatType.CHAT.type -> {
                ChatType.CHAT.viewType
            }
            ChatType.IMAGE.type -> {
                ChatType.IMAGE.viewType
            }
            ChatType.AUDIO.type -> {
                ChatType.AUDIO.viewType
            }
            ChatType.VIDEO.type -> {
                ChatType.VIDEO.viewType
            }
            ChatType.FILE.type -> {
                ChatType.FILE.viewType
            }
            else -> {
                ChatType.CHAT.viewType
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


}