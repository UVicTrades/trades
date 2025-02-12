package ca.uvictrades.trades.configuration

import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class DatabaseConfiguration(
	private val dataSource: DataSource,
) {

    @Bean
    fun jooqDslContext(
        dataSource: DataSource,
    ): DSLContext? {
        return DSL.using(TransactionAwareDataSourceProxy(dataSource), SQLDialect.POSTGRES)
    }

	@Bean
	fun transactionManager(): PlatformTransactionManager = DataSourceTransactionManager(dataSource)
}
