package io.github.koma.snowmetrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class JdbcMetricsService(
    private val jdbcMetricsConfig: JdbcMetricsConfig,
    private val meterRegistry: MeterRegistry,
    private val jdbcTemplate: JdbcTemplate) {

    private val metricsMap = ConcurrentHashMap<String, Long>()

    @Scheduled(fixedDelay = 60_000)
    fun exportMetrics() {
        jdbcMetricsConfig.metrics.forEach { metric ->
            metric.statementsBefore.forEach {
                jdbcTemplate.query(it) {}
            }
            jdbcTemplate.query(metric.query) {
                if (!metricsMap.contains(metric.key())) {
                    metricsMap[metric.key()] = it.getLong(1)
                    Gauge.builder(metric.name) { metricsMap[metric.key()] }
                        .tags(metric.tags.map { entry -> Tag.of(entry.key, entry.value) })
                        .register(meterRegistry)
                } else {
                    metricsMap[metric.key()] = it.getLong(1)
                }
            }
        }
    }
}