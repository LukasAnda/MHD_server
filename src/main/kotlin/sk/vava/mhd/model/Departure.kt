package sk.vava.mhd.model

import com.google.gson.annotations.SerializedName

data class Departure(
        @SerializedName("Delay")
        val delay: String? = "", // 1min
        @SerializedName("DepartureTime")
        val departureTime: String? = "", // 18:04
        @SerializedName("DestinationName")
        val destinationName: String? = "", // Karlova Ves
        @SerializedName("LineName")
        val lineName: String? = "", // 9
        @SerializedName("Notes")
        val notes: String? = "", // k
        @SerializedName("TransportTypeName")
        val transportTypeName: String? = "", // A
        @SerializedName("VehicleNumber")
        val vehicleNumber: Int? = 0 // 7423
)