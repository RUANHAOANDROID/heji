package com.rh.heji.utlis.matisse

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.fragment.app.FragmentActivity
import com.matisse.Matisse
import com.matisse.MimeType
import com.matisse.MimeTypeManager
import com.matisse.entity.CaptureStrategy
import com.matisse.entity.ConstValue
import com.rh.heji.R
import java.util.*


/**
 *Date: 2019-10-23
 *Author: 锅得铁
 *#Matisse图片选择器辅助包装类
 */
object MatisseUtils {
    //companion object
    fun selectSingleImageCrop(activity: FragmentActivity, reqCode: Int) {
        initMatisse(activity, MimeTypeManager.ofImage(), 1, reqCode, true);
    }

    fun selectSingleImage(activity: FragmentActivity, reqCode: Int) {
        initMatisse(activity, MimeTypeManager.ofImage(), 1, reqCode);
    }


    fun selectMultipleImage(activity: FragmentActivity, reqCode: Int) {
        initMatisse(activity, MimeTypeManager.ofImage(), 3, reqCode);
    }

    fun selectMultipleImage(activity: FragmentActivity, reqCode: Int, count: Int) {
        initMatisse(activity, MimeTypeManager.ofImage(), count, reqCode);
    }

    fun selectSingleVideo(activity: FragmentActivity, reqCode: Int) {
        initMatisse(activity, MimeTypeManager.ofVideo(), 1, reqCode);
    }


    fun selectMultipleVideo(activity: FragmentActivity, reqCode: Int) {
        initMatisse(activity, MimeTypeManager.ofVideo(), 9, reqCode);
    }


    private fun initMatisse(
            content: Activity,
            mimeType: EnumSet<MimeType>,
            count: Int,
            reqCode: Int = ConstValue.REQUEST_CODE_CHOOSE,
            isCrop: Boolean = false
    ) {
        val orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Matisse.from(content)
                .choose(mimeType)
                .countable(false)
                .capture(true)
                .isCrop(isCrop)
                .isCircleCrop(true)
                .maxSelectable(count)
                .theme(R.style.CustomMatisseStyle)
                .captureStrategy(
                        CaptureStrategy(
                                true,
                                "com.unistrong.mapoffline.fileProvider",
                                "Leo"
                        )
                )
                .thumbnailScale(0.8f)
                .restrictOrientation(orientation)//横竖屏方向
                .imageEngine(Glide4Engine())
                .forResult(reqCode)
//        Matisse.from(this@ExampleActivity)                              // 绑定Activity/Fragment
//                .choose(
//                        showType,
//                        mediaTypeExclusive
//                )                               // 设置显示类型，单一/混合选择模式
//                .theme(defaultTheme)                                                // 外部设置主题样式
//                .countable(isCountable)                                             // 设置选中计数方式
//                .isCrop(isCrop)                                                     // 设置开启裁剪
//                .isCircleCrop(isCircleCrop)                                                // 裁剪类型，圆形/方形
//                .maxSelectable(maxCount)                                            // 单一选择下 最大选择数量
//                .maxSelectablePerMediaType(
//                        maxImageCount,
//                        maxVideoCount
//                )            // 混合选择下 视频/图片最大选择数量
//                .capture(isOpenCamera)                                              // 是否开启内部拍摄
//                .captureStrategy(                                                   // 拍照设置Strategy
//                        CaptureStrategy(
//                                true,
//                                "${Platform.getPackageName(this@ExampleActivity)}.fileprovider"
//                        )
//                )
//                .thumbnailScale(0.6f)                                         // 图片显示压缩比
//                .spanCount(spanCount)                                               // 资源显示列数
//                .gridExpectedSize(gridSizePx)                                       // 资源显示网格列宽度
//                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)      // 强制屏幕方向
//                .imageEngine(Glide4Engine())                                        // 图片加载实现方式
//                .setLastChoosePicturesIdOrUri(selectedPathIds as ArrayList<String>?)// 预选中
//                .setNoticeConsumer { context, noticeType, title, message ->
//                    showToast(context, noticeType, title, message)
//                }.setStatusBarFuture { params, view ->
//                    // 外部设置状态栏
//                    ImmersionBar.with(params)?.run {
//                        statusBarDarkFont(true)
//                        view?.apply { titleBar(this) }
//                        init()
//                    }
//
//                    // 外部可隐藏Matisse界面中的标题栏
//                    // view?.visibility = if (isDarkStatus) View.VISIBLE else View.GONE
//                }
    }
}