package com.qiet.workflow

import com.qiet.models.Alarm
import com.qiet.models.User
import com.qiet.workflow.QietWorkflow.Singleton.INSTANCE
import java.io.Serializable

class QietWorkflow : Serializable {

    lateinit var alarm: Alarm
    lateinit var user: User

    constructor()

    constructor(
        alarm: Alarm,
        user: User
    ) {
        this.alarm = alarm
        this.user = user
    }


    /** Instance unique non préinitialisée  */
    object Singleton {
        var INSTANCE: QietWorkflow? = null
    }


    /** Point d'accès pour l'instance unique du singleton  */
    fun getInstance(): QietWorkflow {
        if (INSTANCE == null) {
            INSTANCE = QietWorkflow()
        }
        return INSTANCE as QietWorkflow
    }

    fun updateInstance(workflow: QietWorkflow) {
        INSTANCE = workflow
    }

}