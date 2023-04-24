package com.rh.heji.ui.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.UriUtils
import com.rh.heji.App
import com.rh.heji.R
import com.rh.heji.databinding.FragmentSettingBinding
import com.rh.heji.launchIO
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.utils.excel.ReaderFactory
import com.rh.heji.utils.excel.SUFFIX
import java.io.FileDescriptor

class SettingFragment : BaseFragment() {
    private val REQ_CODE_ALIPLAY = 9001
    private val binding: FragmentSettingBinding by lazy {
        FragmentSettingBinding.inflate(
            layoutInflater
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
    }

    override fun initView(rootView: View) {
        binding.inputETC.setOnClickListener { findNavController().navigate(R.id.nav_input_etc) }
        binding.exportQianJi.setOnClickListener { findNavController().navigate(R.id.nav_export) }
        binding.inputAliPlay.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                setType("*/*")
                addCategory(Intent.CATEGORY_OPENABLE)
                //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            }
            startActivityForResult(intent, REQ_CODE_ALIPLAY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_ALIPLAY
            && resultCode == Activity.RESULT_OK
        ) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            data?.data.also { uri ->
                // Perform operations on the document using its URI.
                val parcelFileDescriptor: ParcelFileDescriptor =
                    App.context.contentResolver.openFileDescriptor(uri!!, "r")!!
                val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                val csv = UriUtils.uri2File(uri).absolutePath
                lifecycleScope.launchIO({
                    ReaderFactory.getReader(SUFFIX.CSV)?.readAliPay(csv, result = {

                    })
                })
                LogUtils.d(csv)
            }
        }
    }

    override fun layout() = binding.root

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.title = "设置"
        toolBar.navigationIcon = blackDrawable()
        toolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

}