package fastcampus.aop.part3.chapter04.model

import com.google.gson.annotations.SerializedName


// SerializedName 어노테이션을 통해 key를 매칭시킬 수 있다.
data class Book(
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String,
)