package com.v.oldcall.adapters

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.v.oldcall.R
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.AvatarLoader
import com.v.oldcall.utils.CommonUtil

/**
 * Author:v
 * Time:2020/12/9
 */
class ContactsAdapter : BaseMagicAdapter<ContactEntity, ContactsAdapter.ViewHolder> {
    constructor(mContext: Context) : super(mContext, R.layout.item_contacts)

    private var listener: OnContactAddListener? = null

    override fun bindRealHolder(holder: ContactsAdapter.ViewHolder, position: Int) {
        items[position]?.let {
            if (TextUtils.isEmpty(it.avatar)) {
                holder.ivAvatar.visibility = View.INVISIBLE
                holder.tvAvatar.visibility = View.VISIBLE
                if (it.avatarColor <= 0) {
                    it.avatarColor = CommonUtil.getRandomColor()
                }
                holder.tvAvatar.background =
                    ContextCompat.getDrawable(mContext, it.avatarColor)
                holder.tvAvatar.text = it.name!!.last().toString()
            } else {
                holder.ivAvatar.visibility = View.VISIBLE
                holder.tvAvatar.visibility = View.INVISIBLE
                AvatarLoader.loadAvatar(it, holder.ivAvatar)
            }

            holder.tvPhone.text = it.phone
            holder.tvName.text = it.name
            holder.cb.isChecked = it.isFrequent
            if (position<20)
            Log.w("vvv", "posi=$position phone=${it.phone} isFrequent=${it.isFrequent}")

            holder.itemView.setOnClickListener { _ ->
                holder.cb.isChecked = !holder.cb.isChecked
                val isChecked = holder.cb.isChecked
                if (isChecked == it.isFrequent || listener == null) return@setOnClickListener
                if (isChecked) {
                    listener!!.addContact2Frequent(it, position)
                } else {
                    listener!!.removeContactFromFrequent(it, position)
                }
            }
        }

    }

    override fun getRealViewHolder(itemView: View): ContactsAdapter.ViewHolder =
        ViewHolder(itemView)


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
            }
        }

    }

    override fun extendEmptyHolder(holder: EmptyHolder, position: Int) {
        holder.btn.visibility = View.INVISIBLE
    }


    fun setOnContactAddListener(listener: OnContactAddListener) {
        this.listener = listener
    }

    interface OnContactAddListener {
        fun addContact2Frequent(contact: ContactEntity, position: Int)
        fun removeContactFromFrequent(contact: ContactEntity, position: Int)
    }
}