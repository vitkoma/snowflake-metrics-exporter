package io.github.koma.snowmetrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

private val log = mu.KotlinLogging.logger {}

@Service
class JdbcMetricsService(
    private val jdbcMetricsConfig: JdbcMetricsConfig,
    private val meterRegistry: MeterRegistry,
    private val execService: JdbcExecService) {

    private val metricsMap = ConcurrentHashMap<String, Long>()

    @Scheduled(fixedDelayString = "\${jdbc-metrics-exporter.refresh-seconds}", timeUnit = TimeUnit.SECONDS)
    fun exportMetrics() {
        jdbcMetricsConfig.metrics.forEach {
            val key = it.key()
            try {
                val result = execService.runMetricQueries(it)
                if (!metricsMap.containsKey(key)) {
                    registerMetric(it)
                }
                metricsMap[key] = result
            } catch (e: Exception) {
                log.error("Error querying value for the $key metric.", e)
            }
        }
    }

    private fun registerMetric(metricConfig: JdbcMetricsConfig.MetricConfig) {
        Gauge.builder(metricConfig.name) { metricsMap[metricConfig.key()] }
            .tags(metricConfig.tags.map { entry -> Tag.of(entry.key, entry.value) })
            .register(meterRegistry)
        log.info("Metric " + metricConfig.key() + " registered.")
    }
}