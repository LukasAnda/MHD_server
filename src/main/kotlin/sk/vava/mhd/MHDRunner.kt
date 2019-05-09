package sk.vava.mhd

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PostConstruct
import org.springframework.stereotype.Component
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.vava.mhd.api.MHDService
import sk.vava.mhd.model.Session
import sk.vava.mhd.model.Transport
import sk.vava.mhd.repository.SessionRepository
import sk.vava.mhd.repository.TransportRepository
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


@Component
class MHDRunner {

    @Autowired
    lateinit var service: MHDService

    @Autowired
    lateinit var sessionRepository: SessionRepository

    @Autowired
    lateinit var transportRepository: TransportRepository

    private var sessionId: String = ""

    private val transports = mutableListOf<Transport>()

    val scheduler = Executors.newSingleThreadScheduledExecutor()
    lateinit var timingTask: Future<*>

    private var logger = LoggerFactory.getLogger(MHDRunner::class.java)


    @PostConstruct
    fun init() {
        logger.info("Starting MHD Runner")
        refreshSession()
    }

    fun refreshSession() {
        logger.info("Refreshing session")
        service.service.getNewToken().enqueue {
            onResponse = {
                val cookie = it.headers().get("Set-Cookie")
                sessionId = cookie?.substringBefore(";").toString().plus(";")
                sessionRepository.deleteAll()
                sessionRepository.save(Session(sessionId))
                timingTask = scheduler.scheduleAtFixedRate(
                        { findTransport() },
                        0,
                        1,
                        TimeUnit.MINUTES
                )
            }
            onFailure = {
                logger.error("Failed to refresh session, aborting")
            }
        }
    }

    fun findTransport() {
        service.service.getBusPositions(sessionId).enqueue {
            onResponse = {
                it.body()?.let {
                    logger.info("Pulling transport info returned ${it.size} results")
                    transportRepository.saveAll(it)
                    val sum = it + transports
                    val map = sum.groupBy { it.vehiclenumber }
                            .filter {
                                it.value.distinctBy { it.location?.latitude ?: 0 }.isNotEmpty()
                            }
                    transports.clear()
                    transports.addAll(it)
                }
            }
            onFailure = {
                logger.error("Error getting transport info, refreshing session")
                timingTask.cancel(true)
                refreshSession()
            }
        }
    }

}

fun <T> Call<T>.enqueue(callback: CallBackKt<T>.() -> Unit) {
    val callBackKt = CallBackKt<T>()
    callback.invoke(callBackKt)
    this.enqueue(callBackKt)
}

class CallBackKt<T> : Callback<T> {

    var onResponse: ((Response<T>) -> Unit)? = null
    var onFailure: ((t: Throwable?) -> Unit)? = null

    override fun onFailure(call: Call<T>, t: Throwable) {
        onFailure?.invoke(t)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        onResponse?.invoke(response)
    }

}