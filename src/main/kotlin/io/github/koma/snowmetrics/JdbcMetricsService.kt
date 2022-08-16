package io.github.koma.snowmetrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Service
class JdbcMetricsService(
    private val jdbcMetricsConfig: JdbcMetricsConfig,
    private val meterRegistry: MeterRegistry,
    private val executor: JdbcExecService) {

    private val metricsMap = ConcurrentHashMap<String, Long>()

    @Scheduled(fixedDelayString = "\${jdbc-metrics-exporter.refresh-seconds}", timeUnit = TimeUnit.SECONDS)
    fun exportMetrics() {
        jdbcMetricsConfig.metrics.forEach {
            runStatementsBefore(it)
            runQuery(it)
        }
    }

    private fun runStatementsBefore(metricConfig: JdbcMetricsConfig.MetricConfig) =
        metricConfig.statementsBefore.forEach {
            executor.query(it) {}
        }

    private fun runQuery(metricConfig: JdbcMetricsConfig.MetricConfig) =
        executor.query(metricConfig.query) {
            if (!metricsMap.contains(metricConfig.key())) {
                registerMetric(metricConfig)
            }
            metricsMap[metricConfig.key()] = it.getLong(1)
        }

    private fun registerMetric(metricConfig: JdbcMetricsConfig.MetricConfig) =
        Gauge.builder(metricConfig.name) { metricsMap[metricConfig.key()] }
            .tags(metricConfig.tags.map { entry -> Tag.of(entry.key, entry.value) })
            .register(meterRegistry)
}