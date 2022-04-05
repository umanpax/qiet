package com.qiet.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.qiet.R
import com.qiet.modules.activities.login.LoginActivity
import com.qiet.workflow.QietWorkflow

/**
 * Created by pierre-alexandrevezinet on 27/02/2022.
 *
 */

class Application {
    companion object {

        fun launchActivity(from: AppCompatActivity, to: Class<*>, closePreviousActivity: Boolean) {
            val i = Intent(from, to)
            from.startActivity(i)
            if (closePreviousActivity) {
                from.finish()
            }
        }

        @SuppressLint("MissingPermission")
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun updateQietWorkflow(walkerWorkflow: QietWorkflow) {
            QietWorkflow.Singleton.INSTANCE!!.getInstance().updateInstance(walkerWorkflow)
        }

        fun getQietWorkflow(): QietWorkflow {
            return if (QietWorkflow.Singleton.INSTANCE != null) QietWorkflow.Singleton.INSTANCE!!.getInstance()
            else QietWorkflow()
        }

        fun changeStatusBarColor(activity: Activity, color: Int) {
            val window: Window = activity.window
            window.statusBarColor = ContextCompat.getColor(activity, color)
        }


        fun showSnackBar(context: Context, layout: View, message: String) {
            // showing snack bar with Undo option
            val snackbar = Snackbar
                .make(layout, message, Snackbar.LENGTH_LONG)
            snackbar.setAction("Ok") {
                //DO SOMETHING HERE
            }
            snackbar.setActionTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorQietPrimary
                )
            )
            snackbar.show()
        }


        fun logout(view: Activity, prefsManager: PrefsManager) {
            val builder = AlertDialog.Builder(view,R.style.AlertDialogTheme)
            with(builder)
            {
                setTitle(view.getString(R.string.logout))
                setMessage(view.getString(R.string.logout_message))
                setPositiveButton(
                    view.getString(R.string.yes),
                    DialogInterface.OnClickListener(object : (DialogInterface, Int) -> Unit {
                        override fun invoke(p1: DialogInterface, p2: Int) {
                            prefsManager.setStringInPreferences(ApplicationConstants.EMAIL, "")
                            prefsManager.setStringInPreferences(ApplicationConstants.PASSWORD, "")
                            prefsManager.setBooleanInPreferences(ApplicationConstants.REMEMBERME, false)
                            p1.dismiss()
                            ActivityCompat.finishAffinity(view)
                            val intent = Intent(view, LoginActivity::class.java)
                            view.startActivity(intent)
                        }
                    })
                )
                setNegativeButton(
                    view.getString(R.string.no),
                    DialogInterface.OnClickListener(object : (DialogInterface, Int) -> Unit {
                        override fun invoke(p1: DialogInterface, p2: Int) {
                            p1.dismiss()
                        }
                    })
                )
                show()
            }
        }

    }

}

