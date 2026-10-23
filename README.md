# Sistema Médico

Aplicación web para gestionar usuarios, médicos, disponibilidades y citas de una clínica. El proyecto incluye una interfaz web para pacientes, médicos y administradores, además de una API REST para las operaciones de cada perfil.

## Tecnologías

- **Java 17**
- **Spring Boot 3.5.14**
- **Maven**, con Maven Wrapper incluido
- **Spring Web** para la API REST
- **Spring Data JPA e Hibernate** para el acceso a datos y el mapeo de entidades
- **MySQL** como base de datos
- **Spring Security** y **BCrypt** para el manejo de contraseñas
- **Google Authenticator** para la verificación en dos pasos (2FA)
- **Spring Boot Mail** para el envío de correos
- **HTML, CSS y JavaScript** para las páginas web; la interfaz utiliza Tailwind CSS desde CDN

## Funcionalidades principales

- Registro e inicio de sesión de pacientes con verificación 2FA.
- Portal y paneles diferenciados para pacientes, médicos y administradores.
- Consulta y gestión de citas e historial de citas.
- Registro de disponibilidad por parte de médicos y aprobación de horarios desde el panel administrativo.
- Gestión de médicos y pacientes, actualización de perfil administrativo y estadísticas del sistema.
- Envío de un correo de bienvenida al registrar un paciente.

## Requisitos previos

- Java 17 instalado.
- MySQL instalado y en ejecución.
- Una cuenta de Gmail y una contraseña de aplicación de Google si se desea utilizar el envío de correos.
- Navegador web moderno.

No hace falta instalar Maven por separado si se utiliza el Maven Wrapper incluido en el proyecto.

## Configuración de la base de datos

1. Inicia el servidor MySQL.
2. Ejecuta el script [`DATABASE/database.sql`](./DATABASE/database.sql) para crear la base de datos `clinica_db` y sus tablas. Puedes ejecutarlo desde MySQL Workbench o desde una terminal:

   ```bash
   mysql -u root -p < DATABASE/database.sql
   ```

3. Abre [`src/main/resources/application.properties`](./src/main/resources/application.properties) y configura las credenciales de tu MySQL y del servidor de correo. Por ejemplo:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/clinica_db?serverTimezone=UTC
   spring.datasource.username=TU_USUARIO_MYSQL
   spring.datasource.password=TU_CONTRASENA_MYSQL

   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=TU_CORREO_GMAIL
   spring.mail.password=TU_CONTRASENA_DE_APLICACION
   ```

   Para Gmail, utiliza una **contraseña de aplicación**, no la contraseña habitual de la cuenta. No publiques credenciales reales ni las subas al repositorio.

La configuración actual de JPA usa `ddl-auto=update`: Hibernate puede actualizar el esquema al iniciar la aplicación. Aun así, el script SQL proporciona las tablas iniciales del proyecto.

## Ejecutar la aplicación

Desde la carpeta raíz del proyecto, inicia el backend con el Maven Wrapper:

**Linux o macOS**

```bash
bash ./mvnw spring-boot:run
```

**Windows (PowerShell o CMD)**

```bat
mvnw.cmd spring-boot:run
```

Spring Boot inicia normalmente en el puerto `8080`. Abre la página principal en:

```text
http://localhost:8080/
```

También están disponibles las páginas de acceso y registro:

```text
http://localhost:8080/login.html
http://localhost:8080/registro.html
```

El backend y las páginas estáticas se sirven desde la misma aplicación. Algunas páginas frontend llaman a la API usando `http://localhost:8080`, por lo que la dirección debe coincidir con el host y puerto donde se ejecuta el backend.

## API principal

Los controladores REST se organizan por perfil:

- `/api/auth`: registro, inicio de sesión y verificación 2FA.
- `/api/paciente`: perfil de paciente, consulta de médicos, citas e historial.
- `/api/medico`: perfil del médico, disponibilidades y citas.
- `/api/admin`: gestión administrativa, horarios y estadísticas.

## Pruebas

Para ejecutar las pruebas automatizadas:

**Linux o macOS**

```bash
bash ./mvnw test
```

**Windows**

```bat
mvnw.cmd test
```

La prueba de contexto de Spring necesita que la configuración de la base de datos sea válida y que MySQL esté disponible.

## Estructura del proyecto

```text
DATABASE/
  database.sql
src/
  main/
    java/com/clinica/sistemamedico/
      config/       Configuración de Spring Security y CORS
      controller/   API REST
      dto/          Objetos de transferencia de datos
      model/        Entidades del dominio
      repository/   Acceso a datos mediante JPA
      service/      Lógica de negocio, correo y 2FA
    resources/
      static/       Páginas HTML, estilos y scripts del frontend
      application.properties
  test/             Pruebas automatizadas
pom.xml              Dependencias y configuración de Maven
mvnw / mvnw.cmd      Maven Wrapper para Linux/macOS y Windows
```

## Consideración de seguridad

La configuración actual ernet ni utilizarse en producción sin revisar y reforzar primero la autenticación y autorización del backend, para correccion en caso de que haya fallos de seguridad por temas de version.**.
