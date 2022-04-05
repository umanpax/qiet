package com.qiet.modules.activities.splashscreen

import com.qiet.models.User

interface SplashScreenView{
    fun handleLoginResponse(response : String)
    fun handleUserResponse(response : User)
    fun toggleError(error : String)
}