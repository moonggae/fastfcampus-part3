package fastcampus.aop.part3.chapter04.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


// SerializedName 어노테이션을 통해 key를 매칭시킬 수 있다.
// Parcelize를 통해 직렬화를 할 수 있다.
@Parcelize
data class Book(
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String,
) : Parcelable