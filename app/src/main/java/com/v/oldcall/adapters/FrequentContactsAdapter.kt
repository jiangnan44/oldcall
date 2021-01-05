package com.v.oldcall.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.DialogCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.PopupMenuCompat
import com.v.oldcall.R
import com.v.oldcall.activities.CallActivity
import com.v.oldcall.constants.Keys
import com.v.oldcall.dialogs.ListPopWindow
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.AvatarLoader
import com.v.oldcall.utils.CommonUtil
import com.v.oldcall.utils.ToastManager
import com.v.oldcall.views.ItemTouchHelperAdapter
import java.util.*

/**
 * Author:v
 * Time:2020/12/1
 */
class FrequentContactsAdapter : ItemTouchHelperAdapter,
    BaseMagicAdapter<ContactEntity, FrequentContactsAdapter.ViewHolder> {

    constructor(mContext: Context) : super(mContext, R.layout.item_contacts)

    private lateinit var listItems: List<ListPopWindow.ListItem>

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
                AvatarLoader.loadAvatar(this, holder.ivAvatar)
            }

            holder.tvPhone.text = phone
            holder.tvName.text = name

            holder.itemView.setOnLongClickListener {
                showPopMenu(holder.tvName, position, this)
                true
            }
            holder.itemView.setOnClickListener {
                go2Call(this)
            }
            holder.tvDel.setOnClickListener {
                onItemDelete(position)
            }
        }
    }


    private fun showPopMenu(view: View, position: Int, contact: ContactEntity) {
        with(ListPopWindow(mContext)) {
            anchor = view
            setData(getPopItems())
            setListItemClickListener(object : ListPopWindow.OnListItemClickListener {
                override fun onItemClick(action: Byte) {
                    when (action) {
                        Keys.POP_ACTION_DIAL -> {
                            go2Call(contact)
                        }
                        Keys.POP_ACTION_MODIFY -> {
                            go2ModifyAvatar()
                        }
                        Keys.POP_ACTION_REMOVE -> {
                            onItemDelete(position)
                        }
                    }

                }
            })
            show()
        }

    }


    private fun getPopItems(): List<ListPopWindow.ListItem> {
        if (!::listItems.isInitialized) {
            listItems = arrayListOf(
                ListPopWindow.ListItem(
                    0,
                    mContext.getString(com.v.oldcall.R.string.contact_action_dial),
                    Keys.POP_ACTION_DIAL
                ),
                ListPopWindow.ListItem(
                    0,
                    mContext.getString(com.v.oldcall.R.string.contact_action_modify_avatar),
                    Keys.POP_ACTION_MODIFY
                ),
                ListPopWindow.ListItem(
                    0,
                    mContext.getString(com.v.oldcall.R.string.remove_contact),
                    Keys.POP_ACTION_REMOVE
                )
            )
        }
        return listItems
    }

    private fun go2ModifyAvatar() {
        ToastManager.showShort(mContext, "modify")
    }

    private fun go2Call(entity: ContactEntity) {
        val intent = Intent(mContext, CallActivity::class.java)
        intent.putExtra(Keys.INTENT_MAKE_CALL, entity)
        mContext.startActivity(intent)
    }

    override fun getRealViewHolder(itemView: View): ViewHolder = ViewHolder(itemView)


    inner class ViewHolder(itemView: View) : BaseHolder(itemView) {
        var ivAvatar: ImageView
        var tvAvatar: TextView
        var tvName: TextView
        var tvPhone: TextView
        var cb: CheckBox
        var tvDel: TextView
        var ivDel: ImageView

        init {
            with(itemView) {
                tvAvatar = findViewById(R.id.ic_tv_avatar)
                ivAvatar = findViewById(R.id.ic_iv_avatar)
                tvName = findViewById(R.id.ic_tv_name)
                tvPhone = findViewById(R.id.ic_tv_phone)
                ivDel = findViewById(R.id.ic_iv_del)
                tvDel = findViewById(R.id.ic_tv_del)
                cb = findViewById(R.id.ic_cb)
                cb.visibility = View.INVISIBLE
            }
        }
    }

    interface HandleContactListener {
        fun removeContact(contact: ContactEntity, position: Int)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDelete(position: Int) {
        listener?.removeContact(items[position]!!, position)
    }

}