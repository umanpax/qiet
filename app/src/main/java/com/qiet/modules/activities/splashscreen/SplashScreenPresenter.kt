package com.qiet.modules.activities.splashscreen

import android.content.Intent
import androidx.core.app.ActivityCompat
import com.qiet.models.User
import com.qiet.modules.activities.base.BaseActivity
import com.qiet.workflow.QietWorkflow
import com.qiet.modules.activities.login.LoginActivity
import java.util.*

class SplashScreenPresenter(var view: SplashScreenActivity, var workflow: QietWorkflow) {

    fun setLocale(lang: String) {
        var myLocale: Locale? = null
        myLocale = Locale(lang)
        val res = view.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    fun login(loginpwd: String) {
        when (loginpwd){
            "alexis|qiet" ->  {
                view.handleLoginResponse("OK")
            }
            "bamby|qiet" ->  view.handleLoginResponse("OK")
            else -> view.toggleError("KO")
        }
    }

    fun getUser(loginpwd: String){
        val user = User()
        when(loginpwd){
            "alexis|qiet" -> {
                user.apply {
                    email = "alexis@qiet.fr"
                    lastname = "Bidinot"
                    firstname = "Alexis"
                    position = 1
                    isFirst = true
                }
            }
            "bamby|qiet" -> {
                user.apply {
                    email = "bamby@qiet.fr"
                    lastname = ""
                    firstname = "Bamby"
                    position = 5
                    isFirst = false
                }
            }
        }
        view.handleUserResponse(user)
    }

    fun toQiet() {
        ActivityCompat.finishAffinity(view)
        val intent = Intent(view, BaseActivity::class.java)
        view.startActivity(intent)
    }

    fun toLogin() {
        view.finish()
        val intent = Intent(view, LoginActivity::class.java)
        view.startActivity(intent)
    }
}