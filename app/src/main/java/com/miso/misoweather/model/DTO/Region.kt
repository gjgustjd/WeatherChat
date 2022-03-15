package com.miso.misoweather.model.DTO

import java.io.Serializable

data class Region (
    var bigScale:String="",
    var id:Int=0,
    var location_X:Int =0,
    var location_Y:Int=0,
    var midScale:String="",
    var smallScale:String=""
):Serializable