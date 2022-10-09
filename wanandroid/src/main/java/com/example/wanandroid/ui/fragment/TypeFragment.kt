package com.example.wanandroid.ui.fragment

import Constant
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_type.*
import toast
import top.jowanxu.wanandroidclient.R
import com.example.wanandroid.adapter.TypeAdapter
import com.example.wanandroid.base.BaseFragment
import com.example.wanandroid.bean.TreeListResponse
import com.example.wanandroid.presenter.TypeFragmentPresenterImpl
import com.example.wanandroid.ui.activity.TypeContentActivity
import com.example.wanandroid.view.TypeFragmentView

class TypeFragment : BaseFragment(), TypeFragmentView {
    /**
     * mainView
     */
    private var mainView: View? = null
    private val datas = mutableListOf<TreeListResponse.Data>()
    private val typeFragmentPresenter: TypeFragmentPresenterImpl by lazy {
        TypeFragmentPresenterImpl(this)
    }
    private val typeAdapter: TypeAdapter by lazy {
        TypeAdapter(activity, datas)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView ?: let {
            mainView = inflater.inflate(R.layout.fragment_type, container, false)
        }
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        typeSwipeRefreshLayout.run {
            isRefreshing = true
            setOnRefreshListener(onRefreshListener)
        }
        typeRecyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = typeAdapter
        }
        typeAdapter.run {
            bindToRecyclerView(typeRecyclerView)
            setEmptyView(R.layout.fragment_home_empty)
            onItemClickListener = this@TypeFragment.onItemClickListener
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isFirst) {
            typeFragmentPresenter.getTypeTreeList()
            isFirst = false
        }
    }


    override fun cancelRequest() {
        typeFragmentPresenter.cancelRequest()
    }

    /**
     * scroll to top
     */
    fun smoothScrollToPosition() = typeRecyclerView.scrollToPosition(0)

    /**
     * get Type list Success
     */
    override fun getTypeListSuccess(result: TreeListResponse) {
        result.data.let {
            if (typeSwipeRefreshLayout.isRefreshing) {
                typeAdapter.replaceData(it)
            } else {
                typeAdapter.addData(it)
            }
        }
        typeSwipeRefreshLayout.isRefreshing = false
    }

    /**
     * get Type list Failed
     */
    override fun getTypeListFailed(errorMessage: String?) {
        errorMessage?.let {
            activity.toast(it)
        } ?: let {
            activity.toast(getString(R.string.get_data_error))
        }
        typeSwipeRefreshLayout.isRefreshing = false
    }

    /**
     * get Type list data size equal zero
     */
    override fun getTypeListZero() {
        activity.toast(getString(R.string.get_data_zero))
        typeSwipeRefreshLayout.isRefreshing = false
    }

    /**
     * refresh
     */
    fun refreshData() {
        typeSwipeRefreshLayout.isRefreshing = true
        typeFragmentPresenter.getTypeTreeList()
    }

    /**
     * RefreshListener
     */
    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        refreshData()
    }
    /**
     * ItemClickListener
     */
    private val onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
        if (datas.size != 0) {
            Intent(activity, TypeContentActivity::class.java).run {
                putExtra(Constant.CONTENT_TITLE_KEY, datas[position].name)
                putExtra(Constant.CONTENT_CHILDREN_DATA_KEY, datas[position])
                startActivity(this)
            }
        }
    }
}