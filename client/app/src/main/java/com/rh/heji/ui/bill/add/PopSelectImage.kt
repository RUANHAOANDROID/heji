package com.rh.heji.ui.bill.add

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.lxj.xpopup.core.BottomPopupView

import com.rh.heji.BuildConfig
import com.rh.heji.MainActivity
import com.rh.heji.R
import com.rh.heji.data.db.Image
import com.rh.heji.ui.bill.add.adapter.BillPhotoAdapter
import com.rh.heji.utlis.matisse.MatisseUtils
import com.zhihu.matisse.Matisse


/**
 * Date: 2020/10/12
 * @author: 锅得铁
 * #
 */
class PopSelectImage(private val activity: MainActivity,val selectClick :()->Unit) :
    BottomPopupView(activity) {
    companion object {
        const val SELECT_MAX_COUNT = 3
    }

    private var imageAdapter = BillPhotoAdapter()
    lateinit var selectImgRecycler: RecyclerView


    lateinit var deleteListener: (Image) -> Unit
    lateinit var selectImages: (MutableList<Image>) -> Unit

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
        selectImgRecycler.layoutManager = GridLayoutManager(context, 3)
        //selectImgRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        //selectImgRecycler.addItemDecoration(new GridSpaceItemDecoration(3, 10,10));
        imageAdapter.addFooterView(getFooterView { v: View? ->
            val count = SELECT_MAX_COUNT - imageAdapter.data.size
            if (count <= 0) {
                ToastUtils.showLong("最多只能添加" + SELECT_MAX_COUNT + "张照片")
                return@getFooterView
            }
            selectClick()
//            MatisseUtils.selectMultipleImage(activity, count , launcher =activity.registerForActivityResult(
//                ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(){ result ->
//                    if (result.resultCode != Activity.RESULT_OK) {
//                        return@ActivityResultCallback
//                    }
//                    val  data = result.data
//                    //imageAdapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data))
//                    LogUtils.e("OnActivityResult ${Matisse.obtainOriginalState(data)}")
//                }) )
        })
        selectImgRecycler.adapter = imageAdapter
        val listener =
            OnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
                if (view.id == R.id.imgDelete) {
                    imageAdapter.removeAt(position)
                    if (this::deleteListener.isInitialized) {
                        deleteListener(adapter.getItem(position) as Image)
                    }
                }
            }
        imageAdapter.setOnItemChildClickListener(listener)
        imageAdapter.notifyDataSetChanged()
    }

    fun getImages(): MutableList<Image> {
        return imageAdapter.data
    }

    fun setImages(imgs: MutableList<Image>) {
        imageAdapter.setNewInstance(imgs)
        imageAdapter.notifyDataSetChanged()
        //同时通知ViewModel
        selectImages(imgs)
    }

    fun clear() {
        imageAdapter.setNewInstance(mutableListOf())
    }

    fun getImagesPath(): MutableList<String> {
        val images = mutableListOf<String>()
        if (imageAdapter.data.size > 0) {
            val selectImages = imageAdapter.data.filter {
                !(it.localPath.isNullOrEmpty() && it.onlinePath.isNullOrEmpty())
            }.map {
                var path = ""
                if (it.localPath != null) {
                    path = it.localPath!!
                }
                if (it.onlinePath != null) {
                    path = it.onlinePath!!
                }
                path
            }.toMutableList()
            images.addAll(selectImages)
        }
        return images
    }

    fun setImage(images: MutableList<Image>) {
        if (images.isEmpty()) return
        //服务器返回的是图片的ID、需要加上前缀
        val imagePaths = images.map { image: Image ->
            val onlinePath = image.onlinePath
            if (onlinePath != null && !image.onlinePath!!.contains("http")) { //在线Image路径
                val path = BuildConfig.HTTP_URL + "/image/" + image.onlinePath
                image.onlinePath = path
            }
            image
        }.toMutableList()
        imageAdapter.setNewInstance(imagePaths)
        imageAdapter.notifyDataSetChanged()
    }

    fun setImagesPath(images: MutableList<String>) {

    }
}