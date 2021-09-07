package com.rh.heji.ui.bill.add

import android.content.Context
import com.rh.heji.utlis.matisse.MatisseUtils.selectMultipleImage
import com.rh.heji.MainActivity
import com.lxj.xpopup.core.BottomPopupView
import com.rh.heji.ui.bill.add.adapter.BillPhotoAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import com.rh.heji.R
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.matisse.entity.ConstValue.REQUEST_CODE_CHOOSE
import java.util.ArrayList

/**
 * Date: 2020/10/12
 * Author: 锅得铁
 * #
 */
class PopSelectImage(context: Context, private val activity: MainActivity) :
    BottomPopupView(context) {
    private val selectMaxCount = 3
    private val imageAdapter: BillPhotoAdapter? = BillPhotoAdapter()
    private var selectImgRecycler: RecyclerView? = null

    var images = mutableListOf<String>()
        set(value) {
            if (null != imageAdapter) {
                imageAdapter.setNewInstance(value)
                imageAdapter.notifyDataSetChanged()
            }
            field = value
        }

    lateinit var deleteListener: (MutableList<String>) -> Unit

    private fun getFooterView(listener: OnClickListener): View {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.footer_add, selectImgRecycler, false)
        view.setOnClickListener(listener)
        return view
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_layout_select_ticket_image
    }

    override fun onCreate() {
        super.onCreate()
        selectImgRecycler = findViewById(R.id.selectImgRecycler)
        selectImgRecycler!!.layoutManager = GridLayoutManager(context, 3)
        //selectImgRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        //selectImgRecycler.addItemDecoration(new GridSpaceItemDecoration(3, 10,10));
        imageAdapter!!.addFooterView(getFooterView { v: View? ->
            val count = selectMaxCount - imageAdapter.data.size
            if (count <= 0) {
                ToastUtils.showLong("最多只能添加" + selectMaxCount + "张照片")
                return@getFooterView
            }
            selectMultipleImage(activity, REQUEST_CODE_CHOOSE, count)
        })
        selectImgRecycler!!.adapter = imageAdapter
        val listener =
            OnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
                if (view.id == R.id.imgDelete) {
                    imageAdapter.removeAt(position)
                    if (this::deleteListener.isInitialized) {
                        val urls: MutableList<String> = ArrayList()
                        adapter.data.stream().forEach { o -> urls.add(o.toString()) }
                        deleteListener(urls)
                    }
                }
            }
        imageAdapter.setOnItemChildClickListener(listener)
    }


    fun clear() {
        images = mutableListOf()
    }
}