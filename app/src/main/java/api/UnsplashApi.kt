package api

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface UnsplashApi {

    @GET(
        "services/rest/?method=unsplash.getList" +
                "&api_key=LF8gK8-HGSg" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    fun fetchPhotos(): Call<UnsplashResponse>
    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>

}