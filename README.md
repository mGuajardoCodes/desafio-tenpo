# Proyecto: Aplicación Reactiva con Java y Spring Boot

Este proyecto incluye descripciones en **español** e **inglés** :)

---

## Tecnologías usadas 🚀

- **Java 21**
- **Spring Boot 3.4.1**
- **Redis**
- **WebFlux**
- **PostgreSQL 17**
- **Docker**
- **JUnit 5**
- **Mockito**
- **MockServerNetty**

---

## Decisiones técnicas 🤔

### Arquitectura: Arquitectura en Capas

Aunque la arquitectura hexagonal sería ideal por su separación de responsabilidades, he optado por una arquitectura en capas debido a restricciones de tiempo y conocimientos. Sin embargo, he procurado aplicar buenas prácticas dentro de este enfoque.

Además, la mayor parte del código utiliza un enfoque reactivo para mantener la coherencia con las tecnologías usadas. 📊

### Características principales:

1. **Cálculo con porcentaje dinámico**:

    - Existe un endpoint REST que recibe dos valores: `num1` y `num2`. Estos se suman, se calcula un porcentaje definido en un mock del servicio externo y se suma al resultado.
    - Si los valores son nulos, retorna un `400 BadRequestException`.
    - Para la comunicación con el servicio externo, se usa un mock de WebClient. Si falla, realiza hasta 3 reintentos. Si persiste el fallo, consulta una caché con un TTL de 30 minutos. Si la caché también falla, lanza una excepción notificando que el servicio externo no está disponible.

2. **Caché del porcentaje**:

    - Implementado con Redis, una excelente opción para caché distribuido.
    - Uso de su librería reactiva para mantener la filosofía del proyecto.
    - Configuración realizada mediante Beans que toman valores de las propiedades de la aplicación.
    - La responsabilidad del caché y el servicio de porcentaje están separadas. Si el caché falla, se registran logs y se lanza una excepción al cliente indicando la indisponibilidad del servicio externo.

3. **Reintentos ante fallos del servicio externo**:

    - Los reintentos se configuran desde el archivo `application.properties` o las variables de entorno de Docker Compose.
    - Implementado dentro de la cadena reactiva de WebClient.

4. **Historial de llamadas**:

    - Endpoint para consultar un historial de todas las llamadas realizadas a los endpoints de la API, con soporte para paginación.
    - La captura y guardado de logs es asíncrona.
    - Uso de un filtro para interceptar datos y persistirlos en PostgreSQL mediante R2DBC, logrando comunicación reactiva con la base de datos.

5. **Control de tasas (Rate Limiting)**:

    - Límite de 3 RPM (requests por minuto), configurable.
    - Implementado usando `ReactiveStringRedisTemplate` de Redis.
    - Si se excede el límite, retorna un error HTTP 429 con un mensaje descriptivo.

6. **Manejo de errores HTTP**:

    - He implementado un manejo de errores a nivel global mediante un `GlobalExceptionHandler`, devolviendo un `Mono`  que contiene un mapa con la estructura de error.
    - No uso `ResponseEntity` debido a su falta de soporte reactivo.

7. **Despliegue**:

    - Pocisionate sobre la raiz y Usa Docker Compose.
    - &#x20;Ejecuta:
      ```bash
      sudo docker-compose up --build -d
      ```
    - Una vez se levanten los contenedores puedes  ver logs en tiempo real de la app con:
      ```bash
      sudo docker-compose logs -f app
      ```
    - Para modificar las propiedades, ajusta la sección `environment` del archivo Docker Compose.
    - Para usar en Intelij:
        1. Detén el contenedor con:
           ```bash
           sudo docker ps
           sudo docker stop <containerId>
           ```
        2. Configura el IDE y levanta la app localmente usando el `application.properties` preconfigurado.

8. **Documentación**:

    - Swagger integrado para documentar la API.
    - Incluye una colección para Postman.

9. **Tests**:

    - Principalmente Unit Tests con JUnit y Mockito.
    - Algunos servicios incluyen Integration Tests usando MockServerNetty, preferido frente a WireMock por su soporte reactivo y actualizaciones.

---

# Project: Reactive Application with Java and Spring Boot

This project includes descriptions in **Spanish** and **English** to make it accessible to a wider audience. Explore it! 🌐

---

## Technologies Used 🚀

- **Java 21**
- **Spring Boot 3.4.1**
- **Redis**
- **WebFlux**
- **PostgreSQL 17**
- **Docker**
- **JUnit 5**
- **Mockito**
- **MockServerNetty**

---

## Technical Decisions 🤔

### Architecture: Layered Architecture

Although a hexagonal architecture would be ideal for separating responsibilities, I opted for a layered architecture due to time and knowledge constraints. However, I strived to apply best practices within this approach.

Additionally, most of the code is reactive to align with the technologies used. 📊

### Key Features:

1. **Dynamic Percentage Calculation**:

    - A REST endpoint receives two values: `num1` and `num2`. These are summed, a percentage defined in an external service mock is calculated, and added to the result.
    - If values are null, it returns a `400 BadRequestException`.
    - Uses a WebClient mock for external service communication. If it fails, it retries up to 3 times. If the failure persists, it consults a cache with a TTL of 30 minutes. If the cache also fails, it throws an exception notifying the client that the external service is unavailable.

2. **Percentage Cache**:

    - Implemented with Redis, an excellent choice for distributed caching.
    - Uses the reactive library to maintain project philosophy.
    - Configured using Beans that fetch application properties.
    - Cache and percentage service responsibilities are separated. If the cache fails, logs are recorded, and an exception is thrown to the client indicating external service unavailability.

3. **Retries on External Service Failures**:

    - Retries are configurable from the `application.properties` file or Docker environments.
    - Implemented within the WebClient reactive chain.

4. **Call History**:

    - Endpoint to query the call history of all API endpoints, with pagination support.
    - Log capture and storage are asynchronous.
    - A filter intercepts data and persists it in PostgreSQL using R2DBC, enabling reactive database communication.

5. **Rate Limiting**:

    - Limit of 3 RPM (requests per minute), configurable.
    - Implemented using Redis's `ReactiveStringRedisTemplate`.
    - Exceeding the limit returns an HTTP 429 error with a descriptive message.

6. **HTTP Error Handling**:

    - Implemented with a `GlobalExceptionHandler`, returning a `Mono` containing the error structure.
    - Does not use `ResponseEntity` due to lack of reactive support.

7. **Deployment**:

    - Uses Docker Compose. Run:
      ```bash
      sudo docker-compose up --build -d
      ```
    - To view logs in real time:
      ```bash
      sudo docker-compose logs -f app
      ```
    - To modify properties, adjust the `environment` section of the Docker Compose file.
    - To use in Intelij:
        1. Stop the container with:
           ```bash
           sudo docker ps
           sudo docker stop <containerId>
           ```
        2. Configure the IDE and run the app locally using the preconfigured `application.properties`.

8. **Documentation**:

    - Swagger integrated to document the API.
    - Includes a Postman collection.

9. **Tests**:

    - Mainly Unit Tests with JUnit and Mockito.
    - Some services include Integration Tests using MockServerNetty, preferred over WireMock for its reactive support and updates.

---

