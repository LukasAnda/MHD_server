package sk.vava.mhd.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Session(
        @Id
        val id: String = ""
)