# Snowflake Metrics Exporter

An exporter that runs SQL queries in Snowflake and exposes their results as metrics in Prometheus / OpenMetrics format.

## Quick Start

Prepare an `application.yml` configuration file:
```yaml
spring.datasource:
    url: jdbc:snowflake://<account_identifier>.snowflakecomputing.com/?<connection_params>
    username: <username>
    password: <password>

jdbc-metrics-exporter:
    metrics:
        - name: my_metrics.tables.total
          query: select count(*) from INFORMATION_SCHEMA.TABLES

management.metrics:
  enable:
    all: false
    my_metrics: true
```
Run the exporter using docker:
```
docker run vkoma/snowflake-metrics-exporter:latest -p 8080 -v ./application.yml:/config/application.yml
```
Navigate your browser to http://localhost:8080/actuator/prometheus. You should see
```
# HELP my_metrics_tables_total  
# TYPE my_metrics_tables_total gauge
my_metrics_tables_total <number_of_tables>
```