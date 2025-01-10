package ca.uvictrades.trades.configuration

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatabaseConfiguration(
    private val props: DatabaseProperties,
) {
    @Bean
    fun dataSource(): DataSource? {
        val dataSource =
            DataSourceBuilder
                .create()
                .url(props.url)
                .username(props.username)
                .password(props.password)
                .type(HikariDataSource::class.java)
                .build()
        return dataSource
    }
}