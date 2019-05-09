package sk.vava.mhd.api

import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.lang.NonNull
import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import sk.vava.mhd.api.ApiInterface.Companion.API_BASE_URL
import sk.vava.mhd.model.DepartureBoard
import sk.vava.mhd.model.Stop
import sk.vava.mhd.model.Transport
import java.io.IOException
import java.util.concurrent.TimeUnit


@Service
class MHDService : ApiInterface {
    final val service: RepositoryInterface

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(OkHttpClient()
                        .newBuilder()
                        .callTimeout(2L, TimeUnit.MINUTES)
                        .readTimeout(2L, TimeUnit.MINUTES)
                        .writeTimeout(2L, TimeUnit.MINUTES)
                        .addNetworkInterceptor(UserAgentInterceptor("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36"))
                        .addNetworkInterceptor(XMLHttpRequestInterceptor())
                        .addNetworkInterceptor(HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.NONE))
                        .build())
                .addConverterFactory(GsonConverterFactory.create(Gson().newBuilder().setLenient().create()))
                .build()
        service = retrofit.create(RepositoryInterface::class.java)
    }
}


class UserAgentInterceptor(private val mUserAgent: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().header("User-Agent", mUserAgent).build()
        return chain.proceed(request)
    }
}

class XMLHttpRequestInterceptor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url().pathSegments().contains("DepartureBoard")) {
            request.newBuilder().header("X-Requested-With", "XMLHttpRequest").build()
        }
        return chain.proceed(request)
    }
}

interface ApiInterface {
    companion object {
        val API_BASE_URL = "https://skeleton.dpb.sk/"
    }
}

interface RepositoryInterface {
    @GET("Infotainment/Public/VehicleStateMap")
    fun getNewToken(@Query("_ticket") transportTypeName: String = "973e3385-98cc-47a2-bb26-993fa41f26d2"): Call<ResponseBody>

    @GET("Infotainment/VehicleState/__VehicleStateMapData")
    fun getBusPositions(@Header("Cookie") cookie: String): Call<List<Transport>>

    @GET("Infotainment/DepartureBoard/__StationList")
    fun getStops(@Header("Cookie") cookie: String): Call<List<Stop>>

    @GET("Infotainment/DepartureBoard/__DepartureBoard")
    fun getDepartures(@Header("Cookie") cookie: String, @Query("Passport") passport: Int = 0, @Query("Banister") banister: Int = 0): Call<DepartureBoard>
}

