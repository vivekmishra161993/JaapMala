package com.mtt.jaapmala.data.model

data class MantraDto(
    val name:String,
    val date:String,
    var todayCount:Int,
    var malaCount:Int,
    val lifetimeCount:Int,
    var lifetimeMalaCount:Int,
    var id:Int,
    var currentCount:Int,
    var malaSize: Int = 108
)
