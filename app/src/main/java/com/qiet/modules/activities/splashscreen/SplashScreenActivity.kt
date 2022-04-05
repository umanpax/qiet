package com.qiet.modules.activities.splashscreen

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.*
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_splashscreen.*
import android.os.StrictMode
import androidx.activity.ComponentActivity
import com.qiet.R
import com.qiet.models.User
import com.qiet.utils.Application
import com.qiet.utils.ApplicationConstants
import com.qiet.utils.PrefsManager
import com.qiet.workflow.QietWorkflow
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by pierre-alexandrevezinet on 17/02/2022.
 */

class SplashScreenActivity : ComponentActivity(), SplashScreenView {

    private val STOPSPLASH = 0
    private lateinit var presenter: SplashScreenPresenter
    private lateinit var prefsManager: PrefsManager
    private var workflow = QietWorkflow()
    private var email: String = ""
    private var password: String = ""
    private var emailpassword: String = ""

    /**
     * Default duration for the splash screen (milliseconds)
     */
    private val SPLASHTIME: Long = 2000

    /**
     * Handler to close this activity and to start automatically [MainActivity]
     * after `SPLASHTIME` seconds.
     */
    @Transient
    private val splashHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("ResourceType")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            if (msg.what == STOPSPLASH) {
                initPreferences()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splashscreen)
        Application.changeStatusBarColor(this, R.color.colorWhite)
        ButterKnife.bind(this)
        presenter = SplashScreenPresenter(this, workflow)
        prefsManager = PrefsManager(this)
        val msg = Message()
        msg.what = STOPSPLASH
        splashHandler.sendMessageDelayed(msg, SPLASHTIME)
    }


    override fun handleLoginResponse(response: String) {
        when(response){
            "OK" -> {
                presenter.getUser(emailpassword)
            }
            "KO" -> presenter.toLogin()
        }
    }

    override fun handleUserResponse(response: User) {
        QietWorkflow.Singleton.INSTANCE = QietWorkflow()
        workflow.user = response
        QietWorkflow.Singleton.INSTANCE!!.getInstance().updateInstance(workflow)
        presenter.toQiet()
    }


    override fun toggleError(error: String) {
        presenter.toLogin()
    }



    fun initPreferences() {
        if (prefsManager.keyExistsInPreferences(ApplicationConstants.REMEMBERME)) {
            if (prefsManager.getBooleanFromPreferences(ApplicationConstants.REMEMBERME)) {

                email = prefsManager.getStringFromPreferences(ApplicationConstants.EMAIL)
                password = prefsManager.getStringFromPreferences(ApplicationConstants.PASSWORD)

                email = email.replace("\\s".toRegex(), "")
                password = password.replace("\\s".toRegex(), "")

                prefsManager.setStringInPreferences(ApplicationConstants.EMAIL, email)
                prefsManager.setStringInPreferences(ApplicationConstants.PASSWORD, password)

                emailpassword = "$email|$password"
                if (emailpassword.isNotEmpty()) {
                    presenter.login(emailpassword)
                }
            } else {
                presenter.toLogin()
            }
        }else{
            presenter.toLogin()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}