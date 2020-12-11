package com.v.oldcall.ui.activities

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.v.oldcall.R
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.ui.adapters.ContactsAdapter
import com.v.oldcall.ui.mvps.ContactsContract
import com.v.oldcall.ui.mvps.ContactsPresenter
import com.v.oldcall.ui.views.DividerDecoration
import com.v.oldcall.utils.ToastManager

class AddContactsActivity : ContactsContract.View,
    BaseMvpActivity<ContactsPresenter<ContactsContract.View>>() {


    private var rvContacts: RecyclerView? = null
    private var adapter: ContactsAdapter? = null

    override fun initPresenter() {
        mPresenter = ContactsPresenter()
        mPresenter!!.attach(this)
    }

    override fun getContentLayoutId(): Int {
        return R.layout.activity_choose_contacts
    }

    override fun initView() {
        setToolbarTitle(getString(R.string.add_frequent_contacts))
        rvContacts = findViewById(R.id.aca_rv)
        adapter = ContactsAdapter(this)
        rvContacts!!.let {
            val llm = LinearLayoutManager(this)
            llm.orientation = LinearLayoutManager.VERTICAL
            it.layoutManager = llm
            it.adapter = adapter
            it.addItemDecoration(
                DividerDecoration(
                    llm.orientation,
                    ContextCompat.getColor(this, R.color.gray_7),
                    this,
                )
            )
        }
    }

    override fun initData() {
        mPresenter?.showContacts()
    }

    override fun onContactsGot(list: List<ContactEntity?>?) {
        if (list == null || list.isEmpty()) {
            ToastManager.showShort(this, "there's no contacts in your phone!")
            return
        }
        adapter?.addData(list)
    }

    override fun onContactsSearched(list: List<ContactEntity?>?) {
        TODO("Not yet implemented")
    }

    override fun onAddFrequentContacts(contact: ContactEntity, ret: Boolean) {
        TODO("Not yet implemented")
    }


    override fun onError(msg: String) {
        TODO("Not yet implemented")
    }
}