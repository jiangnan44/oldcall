package com.v.oldcall.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.v.oldcall.R
import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2020/12/1
 */
class RecentContactsAdapter : BaseEmptyAdapter<ContactEntity, ContactsAdapter.ViewHolder> {

    constructor(mContext: Context) : super(mContext, R.layout.item_contacts)

    override fun bindRealHolder(holder: ContactsAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getRealViewHolder(itemView: View): BaseHolder {
        TODO("Not yet implemented")
    }


}