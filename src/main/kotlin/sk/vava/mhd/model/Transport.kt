package sk.vava.mhd.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.springframework.lang.Nullable
import javax.persistence.*

@Entity
data class Transport(
        @Expose
        @SerializedName("IsPublic")
        var isPublic: Boolean = false,
        @Expose
        @SerializedName("Delay")
        var delay: String? = null,
        @Expose
        @SerializedName("CourseName")
        var coursename: String? = null,
        @Expose
        @SerializedName("LineName")
        var linename: String? = null,
        @Expose
        @SerializedName("IsMoving")
        var ismoving: Boolean = false,
        @Expose
        @SerializedName("LocationTimestamp")
        var locationtimestamp: String? = null,
        @Expose
        @SerializedName("Location")
        @Embedded
        @Nullable
        @Column(nullable = true)
        var location: Location? = null,
        @Expose
        @SerializedName("VehicleTransportTypeCaption")
        var vehicletransporttypecaption: String? = null,
        @Expose
        @SerializedName("VehicleTransportTypeName")
        var vehicletransporttypename: String? = null,

        @Expose
        @SerializedName("VehicleNumber")
        @Id
        var vehiclenumber: Int = 0
)
