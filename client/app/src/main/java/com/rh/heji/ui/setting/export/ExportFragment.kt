package com.rh.heji.ui.setting.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.provider.DocumentsContract
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.R
import com.rh.heji.databinding.FragmentExportBinding
import com.rh.heji.render


class ExportFragment : BaseFragment() {
    private val binding: FragmentExportBinding by lazy {FragmentExportBinding.inflate(layoutInflater)  }
    val list = listOf("EXCEL", "CVS")

    //定义静态变量
    companion object {
        const val CREATE_FILE_EXCEL = 1
        const val CREATE_FILE_CVS = 2
    }

    private val viewModel by lazy { ViewModelProvider(this).get(ExportViewModel::class.java) }
    private val popup by lazy { XPopup.Builder(requireContext()).asLoading().setTitle("正在导出") }
    override fun initView(rootView: View) {
        binding.tvExportFormat.setOnClickListener {
            val onSelectListener = OnSelectListener { _, text ->
                popup.show()
                var path =
                    mainActivity.filesDir.absolutePath + "/" + TimeUtils.getNowString() + ".xlsx"
                viewModel.doAction(ExportAction.ExportExcel(path))
            }
            var bottomListPopup = XPopup.Builder(requireContext())
                .asBottomList("选择导出格式", list.toTypedArray(), onSelectListener)
            bottomListPopup.show()
        }
        render(viewModel) {
            when (it) {
                is ExportUiState.Success -> {
                    popup.setTitle(it.path)
                        .postDelayed({ popup.dismiss() }, 1500)
                }
                is ExportUiState.Error -> {
                    popup.setTitle(it.t.message)
                        .postDelayed({ popup.dismiss() }, 1500)
                }
            }
        }
    }

    override fun layout()=binding.root

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "导出"
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