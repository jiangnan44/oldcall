package com.v.oldcall.ui.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.v.oldcall.R
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.CommonUtil

/**
 * Author:v
 * Time:2020/12/9
 */
class ContactsAdapter : BaseEmptyAdapter<ContactEntity, ContactsAdapter.ViewHolder> {
    constructor(mContext: Context) : super(mContext, R.layout.item_contacts)


    override fun bindHolder(holder: ContactsAdapter.ViewHolder, position: Int) {
        items[position]?.let {
            if (it.avatar == null || TextUtils.isEmpty(it.avatar.toString())) {
                holder.ivAvatar.visibility = View.INVISIBLE
                holder.tvAvatar.visibility = View.VISIBLE
                holder.tvAvatar.background =
                    ContextCompat.getDrawable(mContext, CommonUtil.getRandomColor())
                holder.tvAvatar.text = it.name!!.last().toString()
            } else {
                holder.ivAvatar.visibility = View.VISIBLE
                holder.tvAvatar.visibility = View.INVISIBLE
                holder.ivAvatar.setImageURI(it.avatar)
            }

            holder.tvPhone.text = it.phone
            holder.tvName.text = it.name
            holder.cb.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    Toast.makeText(mContext, "checked=$isChecked", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    override fun getViewHolder(itemView: View): ContactsAdapter.ViewHolder = ViewHolder(itemView)

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

}