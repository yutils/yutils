package com.yujing.test

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class User (
    var name: String,
    var sex: Int
)