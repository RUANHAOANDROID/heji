package com.hao.heji.utils.matisse

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.activity.result.ActivityResultLauncher
import com.hao.heji.R
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine


/**
 *  use by https://github.com/lwj1994/Matisse
 * @date 2022/8/2
 * @author 锅得铁
 * @since v1.0
 */
object MatisseUtils {

    const val REQUEST_CODE_CHOOSE = 1008611

    @Deprecated("使用ActivityResultLauncher方式")
    fun selectMultipleImage(
        activity: Activity,
        maxSelectable: Int = 3,
        requestCode: Int = REQUEST_CODE_CHOOSE
    ) {
        Matisse.from(activity)
            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP), false)
            .countable(true)
            //.addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            .gridExpectedSize(activity.resources.getDimension(R.dimen.grid_expected_size).toInt())
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .theme(R.style.CustomMatisseStyle)
            .maxSelectable(maxSelectable)
            .imageEngine(GlideEngine())
            .showPreview(false) // Default is `true`
            .forResult(requestCode)
    }

    fun selectMultipleImage(
        activity: Activity,
        maxSelectable: Int = 3,
        launcher: ActivityResultLauncher<Intent>
    ) {

        Matisse.from(activity)
            .choose(MimeType.ofImage())
            .countable(true)
            //.addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            .gridExpectedSize(activity.resources.getDimension(R.dimen.grid_expected_size).toInt())
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .theme(R.style.CustomMatisseStyle)
            .maxSelectable(maxSelectable)
            .imageEngine(GlideEngine())
            .showPreview(false) // Default is `true`
            .forResult(launcher)
    }
}