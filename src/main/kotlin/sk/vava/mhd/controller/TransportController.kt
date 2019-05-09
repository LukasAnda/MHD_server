package sk.vava.mhd.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sk.vava.mhd.api.MHDService
import sk.vava.mhd.model.DepartureBoard
import sk.vava.mhd.model.Location
import sk.vava.mhd.model.Stop
import sk.vava.mhd.model.Transport
import sk.vava.mhd.repository.SessionRepository
import sk.vava.mhd.repository.StopRepository
import sk.vava.mhd.repository.TransportRepository


@RestController
class TransportController {

    @Autowired
    lateinit var transportRepository: TransportRepository

    @Autowired
    lateinit var sessionRepository: SessionRepository

    @Autowired
    lateinit var stopRepository: StopRepository

    @Autowired
    lateinit var service: MHDService

    private var logger = LoggerFactory.getLogger(TransportController::class.java)

    @GetMapping("/all")
    fun getAllTransports(): List<Transport> {
        logger.info("Get all transports")
        return transportRepository.findAll().toList()
    }

    @GetMapping("/box")
    fun getBoxTransports(
            @RequestParam("latBottomLeft") latBottomLeft: Double,
            @RequestParam("lonBottomLeft") lonBottomLeft: Double,
            @RequestParam("latTopRight") latTopRight: Double,
            @RequestParam("lonTopRight") lonTopRight: Double
    ): List<Transport> {
        logger.info("Get all transports in box [$latBottomLeft,$lonBottomLeft]-[$latTopRight,$lonTopRight]")
        return transportRepository
                .findAll()
                .toList()
                .filter {
                    var location = it.location
                    if (location == null) {
                        location = Location(0, 0.toDouble(), 0.toDouble())
                    }
                    val isInBox = bboxContains(doubleArrayOf(latBottomLeft, latTopRight, lonBottomLeft, lonTopRight), location.latitude!!, location.longitude!!)
                    isInBox && location.longitude != 0.toDouble()
                }
    }

    @GetMapping("/stops")
    fun getAllStops(): List<Stop> {
        logger.info("Finding all stops")
        val stops = stopRepository.findAll()
        if (stops.isEmpty()) {
            val sessionId = sessionRepository.findAll().first().id
            val response = service.service.getStops(sessionId).execute()
            if (response.isSuccessful) {
                response.body()?.let { stops.addAll(it) }
                //Filter all by distinct, for some reason, they are there for mutliple times
                val newStops = stops.distinctBy { Pair(it.passport, it.banister) }
                stops.clear()
                stops.addAll(newStops)
                stopRepository.saveAll(stops)
            }
        }
        return stops.toList()
    }

    @GetMapping("/departures")
    fun getDepartures(@RequestParam("passport") passport: Int, @RequestParam("banister") banister: Int): DepartureBoard {
        logger.info("Getting departures for passport $passport, banister $banister")
        val cookie = sessionRepository.findAll().first().id
        val board = service.service.getDepartures(cookie, passport, banister).execute()
        if (board.isSuccessful && board.body() != null) {
            return board.body()!!
        } else {
            logger.error("No available departure board")
            return DepartureBoard()
        }
    }

    fun bboxContains(bbox: DoubleArray, latitude: Double, longitude: Double): Boolean {
        return bbox[0] <= latitude && latitude <= bbox[1] && bbox[2] <= longitude && longitude <= bbox[3]
    }
}