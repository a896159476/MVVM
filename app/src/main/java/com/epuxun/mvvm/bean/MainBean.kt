package com.epuxun.mvvm.bean

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MainBean(var name:String) {
}