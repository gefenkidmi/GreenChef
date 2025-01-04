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
    var name: String = "",
    var category: String = "",
    var description: String = "",
    var imageUri: String = "",
    var ingredients: List<String> = emptyList(),
    var procedure: String = "",
    var rating: Float = 0.0f,
    var numberOfRatings: Int = 0,
    var ownerId: String = "",
    var lastUpdated: Long = 0
) : Parcelable {

    @Ignore
    constructor() : this("", "", "", "", "", emptyList(), "", 0.0f, 0, "", 0)

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