# 🎭 Ticket Teatro

<p align="center">
  <img src="app/src/main/res/drawable/teatro.jpg" width="180" alt="Ticket Teatro logo"/>
</p>

<p align="center">
  Aplicación Android para la compra de entradas de teatro en segundos.<br/>
  Elige la obra, selecciona tu asiento y descarga tu entrada con código QR — sin colas.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-Java-brightgreen?logo=android" />
  <img src="https://img.shields.io/badge/Backend-Supabase-3ECF8E?logo=supabase" />
  <img src="https://img.shields.io/badge/DB-PostgreSQL-336791?logo=postgresql" />
  <img src="https://img.shields.io/badge/Estado-En%20desarrollo-orange" />
</p>

---

## 📱 Capturas de pantalla

> _Añade aquí capturas de las pantallas principales de la app._

---

## ✨ Funcionalidades

- 🔐 **Registro e inicio de sesión** con contraseñas hasheadas (BCrypt)
- 📅 **Calendario interactivo** de funciones cargadas desde la base de datos
- 🎬 **Detalle de obra** con imagen del cartel, descripción y horario
- 💺 **Selección visual de asientos** con estados: disponible, seleccionado y vendido
- 💳 **Proceso de compra** con resumen y formulario de pago simulado
- 🎫 **Generación de entrada con código QR** única por reserva
- 📄 **Descarga de ticket en PDF** directamente desde el móvil
- 🗂️ **Historial de entradas** del usuario

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| **Android Studio** | IDE de desarrollo |
| **Java** | Lenguaje principal |
| **Supabase** | Backend as a Service (BaaS) |
| **PostgreSQL** | Base de datos relacional |
| **OkHttp** | Llamadas a la API REST |
| **Glide** | Carga de imágenes desde URL |
| **ZXing** | Generación de códigos QR |
| **BCrypt** | Hash seguro de contraseñas |
| **Git / GitHub** | Control de versiones |

---

## 🗄️ Modelo de base de datos

La base de datos está alojada en **Supabase** y sigue un diseño relacional:

```
Usuarios ──< Ventas ──< Detalle_Venta
                            │
Cartelera ──< Funciones ──< Butaca_Funcion >── Butacas >── Zonas
                                │
                            Tickets
```

**Tablas principales:**
- `usuarios` — datos de registro y autenticación
- `cartelera` — catálogo de obras con título, descripción y cartel
- `funciones` — sesiones de cada obra (fecha y hora)
- `butacas` — asientos físicos organizados por zona
- `butaca_funcion` — disponibilidad de cada asiento en cada función
- `ventas` / `detalle_venta` — registro de compras
- `tickets` — entradas generadas con código QR

---

## 🚀 Instalación y configuración

### Requisitos previos

- Android Studio Hedgehog o superior
- JDK 11
- Cuenta en [Supabase](https://supabase.com)

### Pasos

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/nacher1998/Teatro-2.0.git
   cd Teatro-2.0
   ```

2. **Configura las credenciales de Supabase**

   Abre `EventosActivity.java` y `MainActivity.java` y sustituye las constantes:
   ```java
   private static final String SUPABASE_URL = "https://tu-proyecto.supabase.co";
   private static final String SUPABASE_KEY = "tu-api-key";
   ```

3. **Sincroniza el proyecto con Gradle**

   En Android Studio: `File → Sync Project with Gradle Files`

4. **Ejecuta la app**

   Conecta un dispositivo Android o inicia un emulador y pulsa ▶️

---

## 📁 Estructura del proyecto

```
app/src/main/
├── java/com/example/teatro/
│   ├── LandingActivity.java
│   ├── MainActivity.java          # Login
│   ├── RegisterActivity.java
│   ├── EventosActivity.java       # Calendario de funciones
│   ├── DetalleEventoActivity.java
│   ├── SeatSelectionActivity.java
│   ├── ConfirmBookingActivity.java
│   ├── PaymentActivity.java
│   ├── TicketActivity.java
│   ├── MyTicketsActivity.java
│   └── model/
│       └── Evento.java
└── res/
    ├── layout/                    # Layouts de cada pantalla
    ├── drawable/                  # Recursos gráficos y fondos
    └── values/                    # Colores, temas y strings
```

---

## 👥 Autores

| Nombre | GitHub |
|---|---|
| Nour Rekik | [@nacher1998](https://github.com/nacher1998) |
| Ignacio Escobar Reche | — |

---

## 📄 Licencia

Este proyecto ha sido desarrollado como **Proyecto Final de Grado** del ciclo de **Desarrollo de Aplicaciones Web (DAW)** — curso 2024/2025.
