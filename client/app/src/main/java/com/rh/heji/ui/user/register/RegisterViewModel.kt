package com.rh.heji.ui.user.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launch
import java.io.Serializable

class RegisterViewModel : BaseViewModel() {
    lateinit var tel: String
    lateinit var code: String
    lateinit var password: String
    lateinit var password1: String
    val registerLiveData = MutableLiveData<RegisterUser>()

    /**
     * 再次确认密码
     */
    fun checkPassword(): Boolean {
        return password == password1;
    }

    fun register(username: String, tel: String, code: String, password: String): LiveData<RegisterUser> {
        var user = RegisterUser(
                name = username,
                tel = tel,
                password = password,
                code = code)

        launch({
            var response = HejiNetwork.getInstance().register(user)
            registerLiveData.postValue(response.data)
        }, {})
        return registerLiveData;
    }


}

data class RegisterUser(var name: String,
                var password: String,
                var tel: String,
                var code: String):Serializable