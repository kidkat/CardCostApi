### Description of CardCostApi

- OpenApi(Swagger) can be found on http://{server}:{port}/swagger-ui/index.html

### Maven commands
- mvn clean install (install the api with all dependencies on your workspace)
- mvn clean package (package the api)
- mvn spring-boot:run (just start the api via spring, but be careful with configurations) 
### Docker commands
- docker build -t cardcost-api . (create the docker from dockerfile)
- example: docker run -p 8081:8081 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/test -e SPRING_DATASOURCE_USERNAME=user -e SPRING_DATASOURCE_PASSWORD=pass cardcost-api