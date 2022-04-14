package fastcampus.aop.part3.chapter07

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/fa6ab807-cd49-485d-b3ac-01351ed96e43")
    fun getHouseList() : Call<HouseDto>
}