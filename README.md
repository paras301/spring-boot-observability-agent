# spring-boot-observability-agent
Observability Agent using Spring Boot. The story can be found at - 

Some steps:

1. Start the docker using docker-compose up
2. Metrics can be found at - http://localhost:8081/actuator/metrics
3. Prometheus Metrics can be found at - http://localhost:8081/actuator/prometheus
4. Prometheus can be found at - http://localhost:9090
5. Grafana can be found at - can be found at - http://localhost:3000
6. Postgres db available at localhost:5432
7. Postgres can be configured to store the time-series metrics as well