package sk.vava.mhd.model

import com.google.gson.annotations.SerializedName

data class DepartureBoard(
        @SerializedName("Departures")
        val departures: List<Departure?>? = listOf(),
        @SerializedName("StationBanister")
        val stationBanister: Int? = 0, // 1
        @SerializedName("StationName")
        val stationName: String? = "", // Trnavské mýto
        @SerializedName("StationPassport")
        val stationPassport: Int? = 0 // 437
)