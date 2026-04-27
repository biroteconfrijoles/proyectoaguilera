# InventarioUnison

Sistema de inventario de escritorio desarrollado en Java con JavaFX y SQLite. La aplicación permite gestionar usuarios, almacenes y productos con una interfaz gráfica moderna y una capa de datos persistente.

## Arquitectura del proyecto

El proyecto sigue una arquitectura en capas clara:

- **Interfaz de usuario (UI)**
  - `MainApp.java`: punto de entrada JavaFX que carga la vista de login.
  - `LoginController.java`: controla el flujo de autenticación y la navegación al panel principal.
  - `MainController.java`: gestiona la UI principal, las vistas de productos, almacenes y usuarios, y enlaza con los servicios.

- **Modelo de dominio**
  - `Producto.java`: entidad que representa productos en inventario.
  - `Usuario.java`: entidad que representa usuarios del sistema.
  - `Almacen.java`: entidad que representa almacenes físicos.

- **Capa de servicios**
  - `AuthService.java`: lógica de autenticación y gestión de sesión.
  - `ProductoService.java`: validaciones y reglas de negocio para productos.
  - `AlmacenService.java`: validaciones y reglas para almacenes.
  - `UsuarioService.java`: consulta de usuarios mediante ORMLite.

- **Persistencia**
  - `Database.java`: capa de acceso a datos con JDBC para CRUD de usuarios, almacenes y productos.
  - `DatabaseManager.java`: gestor ORMLite para acceso a DAOs de `Usuario`, `Producto` y `Almacen`.

- **Recursos**
  - `src/main/resources/login_view.fxml`
  - `src/main/resources/main_view.fxml`
  - `src/main/resources/styles.css`

- **Build**
  - El proyecto usa Maven para compilar y generar artefactos. La configuración principal está en `pom.xml`.

## Estructura de carpetas

- `src/main/java/mx/unison`: código fuente principal.
- `src/main/java/mx/unison/service`: servicios de negocio.
- `src/main/resources`: vistas FXML y estilos CSS.
- `target/reports/apidocs`: documentación JavaDoc generada en HTML.

## Mejoras respecto a la versión anterior

Las mejoras introducidas en esta versión incluyen:

- **Documentación completa con JavaDoc** en las clases principales y servicios.
- **Generación de documentación HTML** accesible en `target/reports/apidocs/index.html`.
- **Separación de responsabilidades** más clara entre UI, lógica de negocio y acceso a datos.
- **Capa de servicios** que centraliza validaciones y reglas de negocio, evitando que los controladores manejen la lógica directamente.
- **Persistencia organizada** con dos enfoques:
  - `Database.java` para operaciones JDBC directas de CRUD.
  - `DatabaseManager.java` para acceso a DAOs ORMLite en la capa de usuarios y entidades.
- **Mejoras en el mantenimiento** del proyecto mediante comentarios y JavaDoc bien definidos.

## Cómo ejecutar

1. Clonar el repositorio.
2. Ejecutar el comando:
   ```bash
   mvn clean javafx:run
   ```
3. La aplicación abre la pantalla de login.

## Documentación

La documentación JavaDoc está generada en HTML dentro de:

- `target/reports/apidocs/index.html`

Abre ese archivo en el navegador para consultar la API del proyecto.

## Notas

- El proyecto usa Java 17.
- La base de datos SQLite se crea automáticamente con el archivo `InventarioBD.db`.
- El sistema incluye roles básicos: `ADMIN`, `PRODUCTOS` y `ALMACENES`.
