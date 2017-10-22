package co.otaoto.api

import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.gildor.coroutines.retrofit.await

class WebApi : Api {
    private val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
    }

    private val api: OtaotoApi by lazy { retrofit.create(OtaotoApi::class.java) }

    override suspend fun create(secret: String): CreateResult {
        return try {
            val response = api.create(CreateRequest(CreateRequest.Secret(secret))).await()
            CreateSuccess(slug = response.secret.slug, key = response.secret.key)
        } catch (e: HttpException) {
            e.printStackTrace()
            CreateError
        } catch (e: Throwable) {
            e.printStackTrace()
            CreateError
        }
    }

    override suspend fun show(slug: String, key: String): ShowResult {
        return try {
            val response = api.show(slug, key).await()
            response.plain_text?.let { ShowSuccess(it) } ?: ShowError(response.errors ?: "")
        } catch (e: HttpException) {
            e.printStackTrace()
            ShowError("")
        } catch (e: Throwable) {
            e.printStackTrace()
            ShowError("")
        }
    }
}

private const val BASE_URL = "https://otaoto.co/api/"

private interface OtaotoApi {
    @POST("create")
    fun create(@Body body: CreateRequest): Call<CreateResponse>

    @GET("show/{slug}/{key}")
    fun show(@Path("slug") slug: String, @Path("key") key: String): Call<ShowResponse>
}

private data class CreateRequest(val secret: Secret) {
    data class Secret(val plain_text: String)
}

private data class CreateResponse(val secret: Secret) {
    data class Secret(
            val slug: String,
            val link: String,
            val key: String
    )
}

private data class ShowResponse(
        val plain_text: String?,
        val errors: String?
)