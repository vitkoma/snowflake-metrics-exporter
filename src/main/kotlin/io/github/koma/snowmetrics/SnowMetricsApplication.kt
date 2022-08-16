package io.github.koma.snowmetrics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
class SnowMetricsApplication

fun main(args: Array<String>) {
    runApplication<SnowMetricsApplication>(*args)
}
