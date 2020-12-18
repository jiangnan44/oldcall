package com.v.oldcall.adapters

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.v.oldcall.R
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.AvatarLoader
import com.v.oldcall.utils.CommonUtil

/**
 * Author:v
 * Time:2020/12/1
 */
class FrequentContactsAdapter :
    BaseMagicAdapter<ContactEntity, FrequentContactsAdapter.ViewHolder> {

    constructor(mContext: Context) : super(mContext, R.layout.item_contacts)

    private var listener: HandleContactListener? = null
    fun setHandleContactListener(listener: HandleContactListener) {
        this.listener = listener
    }

    override fun bindRealHolder(holder: FrequentContactsAdapter.ViewHolder, position: Int) {
        items[position]?.run {
            if (TextUtils.isEmpty(avatar)) {
                holder.ivAvatar.visibility = View.INVISIBLE
                holder.tvAvatar.visibility = View.VISIBLE
                if (avatarColor <= 0) {
                    avatarColor = CommonUtil.getRandomColor()
                }
                holder.tvAvatar.background =
                    ContextCompat.getDrawable(mContext, avatarColor)
                holder.tvAvatar.text = name!!.last().toString()
            } else {
                holder.ivAvatar.visibility = View.VISIBLE
                holder.tvAvatar.visibility = View.INVISIBLE
                AvatarLoader.loadAvatar(avatar, holder.ivAvatar)
            }

            holder.tvPhone.text = phone
            holder.tvName.text = name

            holder.itemView.setOnClickListener {
                listener?.removeContact(this, position)
            }
        }
    }

    override fun getRealViewHolder(itemView: View): BaseHolder = ViewHolder(itemView)


    inner class ViewHolder(itemView: View) : BaseHolder(itemView) {
        var ivAvatar: ImageView
        var tvAvatar: TextView
        var tvName: TextView
        var tvPhone: TextView
        var cb: CheckBox

        init {
            with(itemView) {
                tvAvatar = findViewById(R.id.ic_tv_avatar)
                ivAvatar = findViewById(R.id.ic_iv_avatar)
                tvName = findViewById(R.id.ic_tv_name)
                tvPhone = findViewById(R.id.ic_tv_phone)
                cb = findViewById(R.id.ic_cb)
                cb.visibility = View.INVISIBLE
            }
        }
    }

    interface HandleContactListener {
        fun removeContact(contact: ContactEntity, position: Int)
    }

}