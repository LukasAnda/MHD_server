package sk.vava.mhd.model

import com.google.gson.annotations.SerializedName
import org.hibernate.annotations.GenericGenerator
import org.springframework.lang.Nullable
import javax.annotation.processing.Generated
import javax.persistence.*

@Entity
data class Stop(
        @SerializedName("Banister")
        val banister: Int? = 0, // 2
        @SerializedName("Location")
        @Nullable
        @Embedded
        @Column(nullable = true)
        val location: Location? = Location(),
        @SerializedName("Name")
        val name: String? = "", // Agátová
        @SerializedName("Passport")
        val passport: Int? = 0, // 81
        @Id
        @GeneratedValue(generator="system-uuid")
        @GenericGenerator(name="system-uuid", strategy = "uuid")
        val id: String = ""
)