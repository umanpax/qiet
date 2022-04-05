package com.qiet.modules.activities.login

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import butterknife.ButterKnife
import com.qiet.R
import com.qiet.models.User
import com.qiet.utils.Application
import com.qiet.utils.ApplicationConstants
import com.qiet.utils.PrefsManager
import com.qiet.workflow.QietWorkflow
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginView {

    /**
     * Created by pierre-alexandrevezinet on 17/02/2022.
     */

    private lateinit var presenter: LoginPresenter
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var rememberMe: CheckBox
    private lateinit var preferences: PrefsManager
    private var emailpassword: String = ""
    private var workflow = QietWorkflow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Application.changeStatusBarColor(this, R.color.colorWhite)
        ButterKnife.bind(this)
        presenter = LoginPresenter(this, workflow)
        preferences = PrefsManager(applicationContext)
        email = findViewById(R.id.editText_user_login)
        password = findViewById(R.id.editText_user_password)
        rememberMe = findViewById(R.id.checkbox_remember_me)

        rememberMe.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                preferences.setBooleanInPreferences(ApplicationConstants.REMEMBERME, true)
                preferences.setStringInPreferences(
                    ApplicationConstants.EMAIL,
                    email.text.toString()
                )
                preferences.setStringInPreferences(
                    ApplicationConstants.PASSWORD,
                    password.text.toString()
                )
            } else {
                preferences.setStringInPreferences(
                    ApplicationConstants.EMAIL,
                    email.text.toString()
                )
                preferences.setStringInPreferences(
                    ApplicationConstants.PASSWORD,
                    password.text.toString()
                )
                preferences.setBooleanInPreferences(ApplicationConstants.REMEMBERME, false)
            }
        }
        button_connexion.setOnClickListener {

            email.setText(email.text.toString().replace("\\s".toRegex(), ""))
            password.setText(password.text.toString().replace("\\s".toRegex(), ""))
            emailpassword = email.text.toString() + "|" + password.text.toString()

            preferences.setStringInPreferences(ApplicationConstants.EMAIL, email.text.toString())
            preferences.setStringInPreferences(
                ApplicationConstants.PASSWORD,
                password.text.toString()
            )

            if (emailpassword != "|") {
                circular_progress_connection.start()
                circular_progress_connection.visibility = View.VISIBLE
                presenter.login(emailpassword)
            } else {
                circular_progress_connection.stop()
                circular_progress_connection.visibility = View.GONE
                Application.showSnackBar(
                    applicationContext,
                    main_constraint_login,
                    getString(R.string.fields_empty_login_pwd)
                )
            }
        }
    }

    override fun handleLoginResponse(response: String) {
        when(response){
            "OK" ->  presenter.getUser(emailpassword)
            "KO" ->    Application.showSnackBar(
                applicationContext,
                main_constraint_login,
                getString(R.string.error_connection)
            )
        }
        circular_progress_connection.stop()
        circular_progress_connection.visibility = View.GONE
    }

    override fun handleUserResponse(response: User) {
        QietWorkflow.Singleton.INSTANCE = QietWorkflow()
        workflow.user = response
        QietWorkflow.Singleton.INSTANCE!!.getInstance().updateInstance(workflow)
        circular_progress_connection.stop()
        circular_progress_connection.visibility = View.GONE
        presenter.toQiet()
    }

    override fun toggleError(error: String) {
        Application.showSnackBar(
            applicationContext,
            main_constraint_login,
            getString(R.string.error_connection))
        circular_progress_connection.stop()
        circular_progress_connection.visibility = View.GONE
    }
}