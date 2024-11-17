# Tres en Raya UPEU

Un proyecto en Java para el juego de **Tres en Raya** (Tic-Tac-Toe) con una API REST usando **Spring Boot** y **JPA**. Este proyecto permite manejar el juego, jugadores y partidas mediante una API REST.

## Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso](#uso)
- [Endpoints de la API](#endpoints-de-la-api)
- [Pruebas](#pruebas)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)

---

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalados los siguientes elementos en tu máquina:

- **Java 11 o superior**
- **Maven**
- **MySQL**
- **Git**

## Instalación y Configuración

### 1. Clonar el Repositorio

Clona este repositorio a tu máquina local:

```bash
git clone https://github.com/GaryFernandoYM/Tres_enrayaUPEU.git
```

### 2. Configuración de la Base de Datos

1. Crea una base de datos en MySQL llamada `tresenraya_db`.
2. Configura tus credenciales en el archivo `application.properties` en `src/main/resources/`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tresenraya_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 3. Construir el Proyecto

En el directorio del proyecto, usa Maven para construir el proyecto:

```bash
mvn clean install
```

### 4. Ejecutar la Aplicación

Inicia la aplicación con el siguiente comando:

```bash
mvn spring-boot:run
```

La aplicación estará disponible en [http://localhost:8080](http://localhost:8080).

## Uso

La API permite gestionar partidas y jugadores de Tres en Raya. Puedes usar **Postman** o `cURL` para hacer solicitudes.

## Endpoints de la API

### 1. Iniciar un Juego

**POST /api/juegos/iniciar**

- **Parámetros**:
  - `esJugadorUnico`: `true` para jugar contra la máquina.
  - `nombreJugadorUno`: Nombre del primer jugador.
  - `nombreJugadorDos` (opcional): Nombre del segundo jugador.
  - `numeroPartidas`: Número de partidas.

Ejemplo de petición:

```json
{
  "esJugadorUnico": false,
  "nombreJugadorUno": "Player1",
  "nombreJugadorDos": "Player2",
  "numeroPartidas": 1
}
```

### 2. Realizar un Movimiento

**PUT /api/juegos/{juegoId}/movimiento**

- **Parámetros**:
  - `posicion`: Índice de la posición en el tablero (0-8).

Ejemplo de petición:

```json
{
  "juegoId": 1,
  "posicion": 4
}
```

### 3. Anular un Juego

**PUT /api/juegos/{juegoId}/anular**

Este endpoint permite anular un juego en curso.

### 4. Reiniciar un Juego

**PUT /api/juegos/{juegoId}/reiniciar**

Reinicia el estado de un juego y sus partidas, volviendo a comenzar desde cero.

### 5. Obtener el Estado de un Juego

**GET /api/juegos/{juegoId}**

Devuelve el estado actual del juego, incluyendo el puntaje, el tablero y el jugador en turno.

## Pruebas

Para probar el proyecto:

1. **Iniciar un Juego**: Envía una solicitud `POST` a `/api/juegos/iniciar` con los datos del juego.
2. **Hacer un Movimiento**: Envía una solicitud `PUT` a `/api/juegos/{juegoId}/movimiento` con la posición deseada.
3. **Verificar Estado del Juego**: Haz una solicitud `GET` a `/api/juegos/{juegoId}` para ver el estado actualizado.

## Contribuciones

Las contribuciones son bienvenidas. Para contribuir:

1. Crea una nueva rama de trabajo:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
2. Realiza los cambios y haz commit:
   ```bash
   git commit -m 'Añadir nueva funcionalidad'
   ```
3. Sube tu rama:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
4. Crea una Pull Request en GitHub.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
