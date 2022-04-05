package com.qiet.models

import java.io.Serializable

class Alarm : Serializable {

    var address = ""
    var longitude = 0.0
    var latitude = 0.0
    var precision = 0
    var enabled = false
    var icon = 0

    constructor()
    constructor(
        address: String,
        longitude: Double,
        latitude: Double,
        precision: Int,
        enabled: Boolean,
        icon: Int
    ) {
        this.address = address
        this.longitude = longitude
        this.latitude = latitude
        this.precision = precision
        this.enabled = enabled
        this.icon = icon
    }


}