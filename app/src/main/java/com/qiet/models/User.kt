package com.qiet.models

import java.io.Serializable

class User : Serializable {

    var email = ""
    var firstname = ""
    var lastname = ""
    var password = ""
    var position = 0
    var isFirst = false

    constructor()
    constructor(
        email: String,
        firstname: String,
        lastname: String,
        password: String,
        position: Int,
        isFirst: Boolean
    ) {
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.password = password
        this.position = position
        this.isFirst = isFirst
    }
}