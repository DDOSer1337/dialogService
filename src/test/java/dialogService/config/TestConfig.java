package dialogService.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@Configuration
public class TestConfig {
    @Bean(initMethod = "start",destroyMethod = "stop")
    public PostgreSQLContainer<?> postgreSQLContainer(){
        return new PostgreSQLContainer<>("postgres:16").withInitScript("init.sql");
    }
    @Bean
    public DataSource dataSource(PostgreSQLContainer<?> postgreSQLContainer){
        var hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(postgreSQLContainer().getJdbcUrl() + "&currentSchema=dialog");
        hikariDataSource.setPassword(postgreSQLContainer().getPassword());
        hikariDataSource.setUsername(postgreSQLContainer().getUsername());
        return hikariDataSource;
    }
}
