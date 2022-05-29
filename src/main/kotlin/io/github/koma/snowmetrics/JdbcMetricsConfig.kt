package io.github.koma.snowmetrics

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jdbc-metrics-exporter")
class JdbcMetricsConfig {

    var refreshSeconds: Int = 60
    lateinit var metrics: List<MetricConfig>

    class MetricConfig {
        lateinit var name: String
        lateinit var query: String
        var tags: Map<String, String> = emptyMap()
        var statementsBefore: Array<String> = arrayOf()

        fun key() = name + '#' + tags.entries.joinToString()
    }
}