package com.example.greenchef.DataClass

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val recipeId: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    var imageUri: String = "",
    val ingredients: List<String> = emptyList(),
    val procedure: String = "",
    val rating: Float = 0.0f,
    val numberOfRatings: Int = 0,
    val ownerId: String = "",
    val lastUpdated: Long = 0
) : Parcelable {

    @Ignore
    constructor() : this("", "", "", "", "", emptyList(), "", 0.0f, 0, "", 0)

    // Implementing Parcelable
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(recipeId)
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(imageUri)
        parcel.writeStringList(ingredients)
        parcel.writeString(procedure)
        parcel.writeFloat(rating)
        parcel.writeInt(numberOfRatings)
        parcel.writeString(ownerId)
        parcel.writeLong(lastUpdated)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }

    // Secondary constructor for Parcelable
    private constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readLong()
    )
}