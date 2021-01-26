package com.rh.heji.ui.setting.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.provider.DocumentsContract
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.R
import com.rh.heji.databinding.ExportFragmentBinding

class ExportFragment : BaseFragment() {
    lateinit var binding: ExportFragmentBinding
    val list = listOf("EXCEL", "CVS")

    //定义静态变量
    companion object {
        const val CREATE_FILE_EXCEL = 1
        const val CREATE_FILE_CVS = 2
    }

    private val viewModel by lazy {ViewModelProvider(this).get(ExportViewModel::class.java) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun initView(view: View?) {
        binding = ExportFragmentBinding.bind(view!!)
        binding.tvExportFormat.setOnClickListener {

            val onSelectListener = OnSelectListener { _, text ->
                var popup = XPopup.Builder(mainActivity).asLoading("正在导出").show()
                var path = mainActivity.filesDir.absolutePath + "/" + TimeUtils.getNowString() + ".xlsx"
                viewModel.exportExcel(path).observe(viewLifecycleOwner, Observer {
                    ToastUtils.showLong(it)
                    if (popup is LoadingPopupView) {
                        popup.setTitle(it)
                        popup.postDelayed({ popup.dismiss() }, 2000)
                    }

                })
            }
            var bottomListPopup = XPopup.Builder(mainActivity).asBottomList("选择导出格式", list.toTypedArray(), onSelectListener)
            bottomListPopup.show()
        }
    }

    override fun layoutId(): Int {
        return R.layout.export_fragment;
    }


    private fun createFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/excel"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, CREATE_FILE_EXCEL)
    }

}