package com.v.oldcall.activities

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.v.oldcall.R
import com.v.oldcall.adapters.FrequentContactsAdapter
import com.v.oldcall.app.App
import com.v.oldcall.constants.Keys
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.mvps.MainContract
import com.v.oldcall.mvps.MainPresenter
import com.v.oldcall.utils.AvatarLoader
import com.v.oldcall.utils.CommonUtil
import com.v.oldcall.utils.PinYinStringUtil
import com.v.oldcall.utils.ToastManager
import com.v.oldcall.views.DividerDecoration
import com.v.oldcall.views.SlideItemHelperCallback


class MainActivity : BaseMvpActivity<MainPresenter<MainContract.View>>(),
    MainContract.View, View.OnClickListener, FrequentContactsAdapter.HandleContactListener {

    private val TAG = "MainActivity"
    private val REQUEST_CODE_EDIT: Int = 0x1
    private var rvContract: RecyclerView? = null
    private lateinit var adapter: FrequentContactsAdapter
    private var modifyPosition = -1

    override fun getContentLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        setNavigationIcon(null)
        val btnAddContract: Button = findViewById(R.id.btn_add_contacts)
        val btnDialler: Button = findViewById(R.id.btn_to_dialler)
        btnAddContract.setOnClickListener(this)
        btnDialler.setOnClickListener(this)

        adapter = FrequentContactsAdapter(this).also {
            it.setEmptyButtonClickListener(this)
            it.setHandleContactListener(this)
        }

        rvContract = findViewById<RecyclerView>(R.id.rv_contacts).also {
            val llm = LinearLayoutManager(this)
            llm.orientation = LinearLayoutManager.VERTICAL
            it.layoutManager = llm
            it.addItemDecoration(
                DividerDecoration(
                    llm.orientation,
                    getColor(this, R.color.teal_3),
                    this
                )
            )
            it.adapter = adapter
            val callback = SlideItemHelperCallback(adapter)
            ItemTouchHelper(callback).attachToRecyclerView(it)
        }
    }

    override fun initPresenter() {
        mPresenter = MainPresenter()
        mPresenter!!.attach(this)
    }

    override fun initData() {
        goCheckPermission()
        mPresenter?.showFrequentContacts()
    }


    override fun showNoContacts(msg: String) {
        adapter.setEmptyTextHint(msg)
        adapter.addNewList(emptyList())
    }

    override fun updateContacts(contactList: List<ContactEntity>) {
        adapter.addNewList(contactList)
    }

    override fun onContactRemoved(removed: Boolean, position: Int) {
        if (removed) {
            ToastManager.showShort(this, getString(R.string.remove_contact_success))
            adapter.remove(position)
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.needRefreshFrequentContacts) {
            mPresenter?.showFrequentContacts()
            App.needRefreshFrequentContacts = false
        }
    }

    override fun onError(msg: String) {
        //nothing to do
    }

    override fun onDestroy() {
        super.onDestroy()
        AvatarLoader.destroy()
    }

    override fun onClick(v: View?) {
        if (null == v || CommonUtil.isFastClick(v)) return
        when (v.id) {
            R.id.btn_to_dialler -> {
                go2Dialler()
            }
            R.id.btn_add_contacts,
            R.id.lev_btn_empty -> {
                go2AddContactsActivity()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && REQUEST_CODE_EDIT == requestCode) {
            data?.let {
                val change = it.getBooleanExtra(Keys.INTENT_EDIT_CHANGE, false)
                if (change) {
                    adapter.notifyItemChanged(modifyPosition)
                }
            }
        }
    }


    private fun go2AddContactsActivity() {
        startActivity(Intent(this, AddContactsActivity::class.java).also {
            it.putExtra(Keys.INTENT_FREQUENT_COUNT, adapter.getRealItemCount())
        })
    }


    private fun go2Dialler() {
        startActivity(Intent(Intent.ACTION_DIAL))
    }

    private fun goCheckPermission() {
        startActivity(Intent(this, PermissionCheckActivity::class.java))
    }

    override fun removeContact(contact: ContactEntity, position: Int) {
        mPresenter?.removeContact(contact, position)
    }

    override fun modifyContact(contact: ContactEntity, position: Int) {
        modifyPosition = position
        startActivityForResult(Intent(this, EditContactActivity::class.java).also {
            it.putExtra(Keys.INTENT_EDIT_CONTACT, contact)
        }, REQUEST_CODE_EDIT)
    }
}