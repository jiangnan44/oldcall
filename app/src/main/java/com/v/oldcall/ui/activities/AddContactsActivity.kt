package com.v.oldcall.ui.activities

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.v.oldcall.R
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.ui.adapters.ContactsAdapter
import com.v.oldcall.ui.mvps.ContactsContract
import com.v.oldcall.ui.mvps.ContactsPresenter
import com.v.oldcall.ui.views.AlphabetLayout
import com.v.oldcall.ui.views.DividerDecoration
import com.v.oldcall.utils.ToastManager

class AddContactsActivity : ContactsContract.View,
    BaseMvpActivity<ContactsPresenter<ContactsContract.View>>() {

    private var mScrollState = -1

    private lateinit var etSearch: EditText
    private lateinit var ivSearch: ImageView
    private lateinit var sortLayout: AlphabetLayout
    private lateinit var rvContacts: RecyclerView
    private lateinit var adapter: ContactsAdapter

    override fun initPresenter() {
        mPresenter = ContactsPresenter()
        mPresenter!!.attach(this)
    }

    override fun getContentLayoutId(): Int {
        return R.layout.activity_choose_contacts
    }

    override fun initView() {
        setToolbarTitle(getString(R.string.add_frequent_contacts))
        with(mToolbar!!) {
            etSearch = this.findViewById(R.id.aca_et_search)
            ivSearch = this.findViewById(R.id.aca_iv_search)
        }
        sortLayout = findViewById(R.id.aca_al)
        rvContacts = findViewById(R.id.aca_rv)
        adapter = ContactsAdapter(this)
        val footer = LayoutInflater.from(this)
            .inflate(R.layout.view_no_more, window.decorView as ViewGroup, false)
        adapter.addFooterView(footer)
        adapter.setEmptyTextHint(getString(R.string.no_contacts_now))
        rvContacts.let {
            it.isNestedScrollingEnabled = false
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
        connectData()
        initListener()
    }

    private fun initListener() {
        ivSearch.setOnClickListener {
            val content = etSearch.text.trim().toString()
            if (!TextUtils.isEmpty(content)) {
                mPresenter?.searchContacts(content)
            }
        }

        etSearch.addTextChangedListener {
            if (adapter.getRealItemCount() <= 0) {
                return@addTextChangedListener
            }
            val keyWord = it.toString()
            if (TextUtils.isEmpty(keyWord)) {
                getAllContacts()
            }
        }
    }


    private fun connectData() {
        sortLayout.setOnLetterChooseListener(object : AlphabetLayout.OnLetterChooseListener {
            override fun onLetterChoose(letter: String) {
                for (i in 0 until adapter.getRealItemCount()) {
                    if (adapter.getItem(i).alpha.equals(letter)) {
                        rvContacts.scrollToPosition(i)
                        break
                    }
                }
            }

        })

        rvContacts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                mScrollState = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mScrollState == -1) {
                    return
                }
                rvContacts.let {
                    val llm = it.layoutManager as LinearLayoutManager
                    val firstPosition = llm.findFirstVisibleItemPosition()
                    sortLayout.updateCurrentLetter(adapter.getItem(firstPosition).alpha)
                    if (mScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        mScrollState = -1
                    }
                }
            }
        })
    }

    override fun initData() {
        mPresenter?.showContacts()
    }

    private fun getAllContacts() {
        mPresenter?.showContacts()
    }

    override fun onContactsGot(list: List<ContactEntity>?) {
        if (list == null || list.isEmpty()) {
            ToastManager.showShort(this, "there's no contacts in your phone!")
            return
        }
        adapter.addNewList(list)
    }

    override fun onContactsSearched(list: List<ContactEntity>?) {
        if (list == null || list.isEmpty()) {
            ToastManager.showShort(this, "No result found!")
            return
        }
        adapter.addNewList(list)
        sortLayout.updateCurrentLetter(list[0].alpha)
    }

    override fun onAddFrequentContacts(contact: ContactEntity, ret: Boolean) {
        TODO("Not yet implemented")
    }


    override fun onError(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}