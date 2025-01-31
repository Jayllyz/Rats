package com.rats.data.dto

import android.os.Parcel
import android.os.Parcelable

data class UserLocationDTO (
    val latitude: Double,
    val longitude: Double,
) : Parcelable { // Parcelable est utilisé à cause du Intent, qui peut pas passer des objets complexes
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserLocationDTO> {
        override fun createFromParcel(parcel: Parcel): UserLocationDTO {
            return UserLocationDTO(parcel)
        }

        override fun newArray(size: Int): Array<UserLocationDTO?> {
            return arrayOfNulls(size)
        }
    }
}