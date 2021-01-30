package com.rh.heji.ui.user.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppCache
import com.rh.heji.utlis.http.basic.BaseCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    lateinit var tel: String;
    lateinit var code: String;
    lateinit var password: String;
    lateinit var password1: String;

    /**
     * 再次确认密码
     */
    fun checkPassword(): Boolean {
        return password == password1;
    }

    fun register(tel: String, code: String, password: String): LiveData<String> {
        var user = User();
        user.name = tel;
        user.tel =tel;
        user.password = password;
        user.code = code;
        viewModelScope.launch() {
            withContext(Dispatchers.IO) {
                var response = AppCache.instance.heJiServer.register(user).execute();
                if (response.isSuccessful){
                    if (response.code()==200){
                        ToastUtils.showLong(response.message())
                    }
                }
            }
        }

        return MutableLiveData();
    }

    class User {
        var name: String? = null
        var password: String? = null
        var tel: String? = null
        var code: String? = null
    }
}