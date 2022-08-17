package io.github.koma.snowmetrics

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JdbcExecService(private val jdbcTemplate: JdbcTemplate) {

    @Transactional
    fun runMetricQueries(metricConfig: JdbcMetricsConfig.MetricConfig): Long {
        runStatementsBefore(metricConfig)
        return runQuery(metricConfig)
    }

    private fun runStatementsBefore(metricConfig: JdbcMetricsConfig.MetricConfig) =
        metricConfig.statementsBefore.forEach {
            jdbcTemplate.query(it) {}
        }

    private fun runQuery(metricConfig: JdbcMetricsConfig.MetricConfig): Long {
        var result = Long.MIN_VALUE
        jdbcTemplate.query(metricConfig.query) {
            result = it.getLong(1)
            if (it.wasNull()) {
                result = Long.MIN_VALUE
            }
        }
        return result
    }

}