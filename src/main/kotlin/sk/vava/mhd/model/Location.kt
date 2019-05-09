package sk.vava.mhd.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.springframework.lang.Nullable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Location(
        @Expose
        @SerializedName("Altitude")
        @Nullable
        @Column(nullable = true)
        var altitude: Int? = 0,
        @Expose
        @SerializedName("Longitude")
        @Nullable
        @Column(nullable = true)
        var longitude: Double? = 0.toDouble(),
        @Expose
        @SerializedName("Latitude")
        @Nullable
        @Column(nullable = true)
        var latitude: Double? = 0.toDouble()
)