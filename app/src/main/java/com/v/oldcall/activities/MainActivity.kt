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
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.mvps.MainContract
import com.v.oldcall.mvps.MainPresenter
import com.v.oldcall.utils.AvatarLoader
import com.v.oldcall.utils.CommonUtil
import com.v.oldcall.utils.ToastManager
import com.v.oldcall.views.DividerDecoration
import com.v.oldcall.views.SlideItemHelperCallback
import com.v.oldcall.views.SwipeRecyclerView


class MainActivity : BaseMvpActivity<MainPresenter<MainContract.View>>(),
    MainContract.View, View.OnClickListener, FrequentContactsAdapter.HandleContactListener {

    private val TAG = "MainActivity"
    private var rvContract: RecyclerView? = null
    private lateinit var adapter: FrequentContactsAdapter


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

    override fun initData() {
        goCheckPermission()
        mPresenter?.showFrequentContacts()
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.showFrequentContacts()
    }

    override fun showNoContacts(msg: String) {
        adapter.setEmptyTextHint(msg)
        adapter.addNewList(emptyList())
    }

    override fun onContactRemoved(removed: Boolean, position: Int) {
        if (removed) {
            ToastManager.showShort(this, getString(R.string.remove_contact_success))
            adapter.remove(position)
        }
    }

    override fun updateContacts(contactList: List<ContactEntity>) {
        adapter.addNewList(contactList)
    }


    override fun onError(msg: String) {
        TODO("Not yet implemented")
    }

    override fun initPresenter() {
        mPresenter = MainPresenter()
        mPresenter!!.attach(this)
    }

    override fun onClick(v: View?) {
        if (null == v || CommonUtil.isFastClick(v)) return
        when (v.id) {
            R.id.btn_to_dialler -> {
                go2Dialler();
            }
            R.id.btn_add_contacts,
            R.id.lev_btn_empty -> {
                go2AddContactsActivity()
            }
        }
    }

    private fun go2AddContactsActivity() {
        val intent = Intent(this, AddContactsActivity::class.java)
        startActivity(intent)
    }


    private fun go2Dialler() {
        val intent = Intent(Intent.ACTION_DIAL)
        startActivity(intent)
    }

    private fun goCheckPermission() {
        val intent = Intent(this, PermissionCheckActivity::class.java)
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        AvatarLoader.destroy()
    }

    override fun removeContact(contact: ContactEntity, position: Int) {
        Log.w("vvv", "removeContact posi=$position id=${contact.id}")
        mPresenter?.removeContact(contact, position)
    }
}