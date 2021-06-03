package com.rh.heji

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Dealer
import com.rh.heji.data.db.ErrorLog
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.BillEntity
import com.rh.heji.network.request.CategoryEntity
import com.rh.heji.service.work.DataSyncWork
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.CrashInfo
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModule(application: Application) : AndroidViewModel(application) {
    val network: HejiNetwork = HejiNetwork.getInstance()
    private val billRepository = BillRepository()
    private val categoryRepository = CategoryRepository()
    val asyncLiveData = MediatorLiveData<Any>()
    val user:JWTParse.User by lazy { JWTParse.getUser(AppCache.instance.token.tokenString) }
    init {
        launchIO({
            fakeData()
            LogUtils.getConfig().globalTag = "TAG"
            initCarshTool()
        }, {
            it.printStackTrace()
        })
    }

     fun initCarshTool() {
        if (ActivityCompat.checkSelfPermission(
                AppCache.instance.context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            CrashUtils.init(AppCache.instance.storage("Crash"), object:CrashInfo(){
                override fun onCrash(crashInfo: CrashUtils.CrashInfo) {
                    super.onCrash(crashInfo)
                    launchIO({
                        errorLog?.let {
                            HejiNetwork.getInstance().logUpload(it)
                        }
                    },{})
                }
            })
        }
    }

    fun billPush(billEntity: BillEntity) {
        launchIO({ billRepository.pushBill(billEntity) })

    }

    fun billDelete(_id: String) {
        launchIO({
            AppDatabase.getInstance().billDao().preDelete(_id)
            billRepository.deleteBill(_id)
        })

    }

    fun billUpdate(billEntity: BillEntity) {
        launchIO({ billRepository.updateBill(billEntity) })
    }

    fun billPull() {
        launchIO({ billRepository.pullBill() })
    }

    fun categoryPush(categoryEntity: CategoryEntity) {
        launchIO({ categoryRepository.pushCategory(categoryEntity) })
    }

    fun categoryPull() {
        launchIO({ categoryRepository.pullCategory() })
    }

    fun asyncData() {
        launchIO({
            var dataAsyncWork = DataSyncWork()
            //DataSyncWork 方法执行在IO线程
            dataAsyncWork.asyncBills()
            dataAsyncWork.asyncCategory()
            asyncLiveData.postValue("OJBK")
        }, {
            it.printStackTrace()
        })

    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun launchIO(
        block: suspend () -> Unit,
        error: suspend (Throwable) -> Unit = { it.printStackTrace() }
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            block()
        } catch (e: Throwable) {
            error(e)
            e.printStackTrace()
        }
    }

    private fun launch(
        block: suspend () -> Unit,
        error: suspend (Throwable) -> Unit = { it.printStackTrace() }
    ) = viewModelScope.launch() {
        try {
            block()
        } catch (e: Throwable) {
            error(e)
            e.printStackTrace()
        }
    }

    fun fakeData() {
        val u1 = Dealer("锅得铁")
        val u2 = Dealer("谢大脚")
        val u3 = Dealer("赵四")
        val u4 = Dealer("刘能")
        val u5 = Dealer("永强")
        AppDatabase.getInstance().dealerDao().insert(u1)
        AppDatabase.getInstance().dealerDao().insert(u2)
        AppDatabase.getInstance().dealerDao().insert(u3)
        AppDatabase.getInstance().dealerDao().insert(u4)
        AppDatabase.getInstance().dealerDao().insert(u5)

        val startCount = AppCache.instance.kvStorage?.decodeInt("start", 0)
        if (startCount == 1) {
            val c0_0 = Category(ObjectId().toString(), "其他", 0, -1)
            val c0_1 = Category(ObjectId().toString(), "其他", 0, 1)
            AppDatabase.getInstance().categoryDao().insert(c0_0)
            AppDatabase.getInstance().categoryDao().insert(c0_1)
        }
        if (startCount == 1) {
            val c1 = Category(ObjectId().toString(), "吃饭", 0, -1)
            val c2 = Category(ObjectId().toString(), "交通", 0, -1)
            val c3 = Category(ObjectId().toString(), "文具", 0, -1)
            val c4 = Category(ObjectId().toString(), "保险", 0, -1)
            val c5 = Category(ObjectId().toString(), "随礼", 0, -1)
            val c6 = Category(ObjectId().toString(), "日常", 0, 1)

            AppDatabase.getInstance().categoryDao().insert(c5)
            AppDatabase.getInstance().categoryDao().insert(c4)
            AppDatabase.getInstance().categoryDao().insert(c3)
            AppDatabase.getInstance().categoryDao().insert(c2)
            AppDatabase.getInstance().categoryDao().insert(c1)

            AppDatabase.getInstance().categoryDao().insert(c6)


        }
    }
}