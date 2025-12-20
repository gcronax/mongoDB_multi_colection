Claro 👍 Aquí tienes un **README completo y claro** para tu programa. Puedes copiarlo tal cual en un archivo llamado **`README.md`** en la raíz del proyecto.

---

# 📦 Proyecto Concesionario – Kotlin + MongoDB en Memoria

## 📌 Descripción

Este proyecto es una aplicación de consola desarrollada en **Kotlin** que simula la gestión de un **concesionario** utilizando una base de datos **MongoDB en memoria**.
Permite gestionar **coches, clientes y facturas**, importar y exportar datos en formato **JSON**, y realizar distintas consultas mediante un menú interactivo.

La base de datos se crea en memoria al iniciar el programa y se destruye al finalizar, garantizando que los datos se carguen y guarden desde archivos JSON.

---

## 🛠️ Tecnologías utilizadas

* **Kotlin**
* **MongoDB en memoria** (`de.bwaldvogel.mongo`)
* **MongoDB Java Driver**
* **JSON** (`org.json`)
* **Gradle / IntelliJ / Android Studio**

---

## 📂 Estructura del proyecto

```
src/
 └── main/
     ├── kotlin/
     │   └── Main.kt
     └── resources/
         ├── cars.json
         ├── clientes.json
         └── facturas.json
```

---

## 🗄️ Base de datos

* **Nombre de la base de datos:** `concesionario`

### Colecciones:

* `cars` → coches del concesionario
* `clientes` → clientes registrados
* `facturas` → facturas de compra

La base de datos se ejecuta **completamente en memoria**, por lo que:

* Al iniciar el programa se importan los datos desde los archivos JSON.
* Al cerrar el programa, los datos se exportan de nuevo a los archivos JSON.

---

## ▶️ Funcionamiento del programa

### 1️⃣ Inicio

Al ejecutar el programa:

* Se inicia un servidor MongoDB en memoria.
* Se importan los datos desde los archivos JSON a las colecciones.
* Se muestra el menú principal.

### 2️⃣ Menú principal

```
1. Menu Coches
2. Menu Facturas
3. Menu Clientes
4. Mostrar factura
5. Mostrar coche con mayor precio en la factura
6. Mostrar clientes y coches comprados
7. Salir
```

El usuario introduce la opción deseada y el programa valida la entrada.

---

## 📥 Importación de datos

La función `importarBD()`:

* Lee un archivo JSON.
* Convierte cada objeto JSON en un `Document`.
* Elimina el campo `_id` si existe para que MongoDB genere uno nuevo.
* Borra la colección existente antes de insertar los datos.

---

## 📤 Exportación de datos

La función `exportarBD()`:

* Recorre todos los documentos de la colección.
* Los guarda en un archivo JSON con formato legible (indentado).
* Se ejecuta automáticamente al salir del programa.

---

## ✅ Validaciones

El programa incluye validaciones para:

* Entrada de números enteros (`isInt`)
* Entrada de números decimales (`isDouble`)
* Entrada de texto no vacío (`isString`)

Esto evita errores por entradas incorrectas del usuario.

---

## 🧹 Cierre del programa

Al seleccionar **Salir**:

* Se exportan todas las colecciones a sus archivos JSON.
* Se cierra el cliente MongoDB.
* Se apaga el servidor en memoria.


