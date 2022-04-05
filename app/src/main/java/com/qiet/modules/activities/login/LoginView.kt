package com.qiet.modules.activities.login

import com.qiet.models.User

interface LoginView{
    fun handleLoginResponse(response : String)
    fun handleUserResponse(response : User)
    fun toggleError(error : String)
}