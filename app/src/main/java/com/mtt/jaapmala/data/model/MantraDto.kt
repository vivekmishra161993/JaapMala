package com.mtt.jaapmala.data.model

data class MantraDto(
    val name:String,
    val date:String,
    var todayCount:Int,
    var malaCount:Int,
    val lifetimeCount: Long,
    var lifetimeMalaCount: Long,
    var id:Int,
    var currentCount:Int,
    var malaSize: Int = 108
)
