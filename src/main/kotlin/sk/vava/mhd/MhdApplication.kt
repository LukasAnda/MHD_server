package sk.vava.mhd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.boot.SpringApplication


@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication

class MhdApplication {


    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(MhdApplication::class.java, *args)
        }
    }
}
