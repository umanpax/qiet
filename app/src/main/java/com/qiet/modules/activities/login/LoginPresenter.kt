package com.qiet.modules.activities.login

import android.content.Intent
import androidx.core.app.ActivityCompat
import com.qiet.models.User
import com.qiet.modules.activities.base.BaseActivity
import com.qiet.utils.PrefsManager
import com.qiet.workflow.QietWorkflow

class LoginPresenter(var view: LoginActivity, var workflow: QietWorkflow) {

    private var prefsManager = PrefsManager(view)

    fun login(loginpwd: String) {
        when (loginpwd){
            "alexis|qiet" ->  view.handleLoginResponse("OK")
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

}