package io.github.koma.snowmetrics

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet

@Service
class JdbcExecService(private val jdbcTemplate: JdbcTemplate) {

    @Transactional
    fun query(sql: String, rowHandler: (ResultSet) -> Unit) = jdbcTemplate.query(sql, rowHandler)

}