import com.mongodb.client.MongoCollection
import java.util.InputMismatchException
import org.bson.json.JsonWriterSettings
import java.io.File
import com.mongodb.client.MongoClients
import org.bson.Document
import org.json.JSONArray

import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend

import com.mongodb.client.MongoClient
import kotlin.collections.contains
import kotlin.io.printWriter
import kotlin.io.readText
import kotlin.io.use
import kotlin.ranges.until
import kotlin.text.isBlank
import kotlin.text.toDoubleOrNull
import kotlin.text.toIntOrNull


lateinit var servidor: MongoServer
lateinit var cliente: MongoClient
lateinit var uri: String

lateinit var coleccionClientes: MongoCollection<Document>
lateinit var coleccionCoches: MongoCollection<Document>
lateinit var coleccionFacturas: MongoCollection<Document>


const val NOM_BD = "concesionario"
const val NOM_COLECCION_COCHES = "cars"
const val NOM_COLECCION_CLIENTES = "clientes"
const val NOM_COLECCION_FACTURAS = "facturas"


fun conectarBD() {
    servidor = MongoServer(MemoryBackend())
    val address = servidor.bind()
    uri = "mongodb://${address.hostName}:${address.port}"

    cliente = MongoClients.create(uri)
    coleccionClientes = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION_CLIENTES)
    coleccionCoches = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION_COCHES)
    coleccionFacturas = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION_FACTURAS)

    println("Servidor MongoDB en memoria iniciado en $uri")
}

fun desconectarBD() {
    cliente.close()
    servidor.shutdown()
    println("Servidor MongoDB en memoria finalizado")
}

fun main() {
    conectarBD()

    importarBD("src/main/resources/cars.json", coleccionCoches)
    importarBD("src/main/resources/clientes.json", coleccionClientes)
    importarBD("src/main/resources/facturas.json", coleccionFacturas)

    menu()

    exportarBD(coleccionCoches,"src/main/resources/cars.json")
    exportarBD(coleccionClientes,"src/main/resources/clientes.json")
    exportarBD(coleccionFacturas,"src/main/resources/facturas2.json")

    desconectarBD()
}

fun menu() {
    var itera = true
    do {
        println()
        println("   Selecciona una opcion: ")
        println("1. Menu Coches")
        println("2. Menu Facturas")
        println("3. Menu Clientes")
        println("4. Salir")
        try {
            val select: Int = isInt()
            when (select) {
                1 -> {
                    menuCoches()
                }
                2 -> {
                    menuFacturas()
                }
                3 -> {
                    menuClientes()
                }
                4 -> {
                    itera = false
                }

                else -> {
                    println("Opcion no valida. Por favor, selecciona una opcion del 1 al 4.")
                }
            }

        } catch (e: InputMismatchException) {
            println("Error: Debes introducir un numero valido.")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    } while (itera)
}

fun isInt():Int{
    while (true){
        val entrada= readln().toIntOrNull()
        if (entrada==null){
            println("Dame un número valido")
        }else{
            return entrada
        }
    }
}
fun isDouble(): Double{
    while (true){
        val entrada= readln().toDoubleOrNull()
        if (entrada==null){
            println("Dame un número valido(Double)")
        }else{
            return entrada
        }
    }
}
fun isString(): String{
    while (true){
        val entrada= readln()
        if (entrada.isBlank()){
            println("Dame un string valido")
        }else{
            return entrada
        }
    }
}


fun importarBD(rutaJSON: String, coleccion: MongoCollection<Document>) {
    println("Iniciando importación de datos desde JSON...")

    val jsonFile = File(rutaJSON)
    if (!jsonFile.exists()) {
        println("No se encontró el archivo JSON a importar")
        return
    }

    // Leer JSON del archivo
    val jsonText = try {
        jsonFile.readText()
    } catch (e: Exception) {
        println("Error leyendo el archivo JSON: ${e.message}")
        return
    }

    val array = try {
        JSONArray(jsonText)
    } catch (e: Exception) {
        println("Error al parsear JSON: ${e.message}")
        return
    }

    // Convertir JSON a Document y eliminar _id si existe
    val documentos = mutableListOf<Document>()
    for (i in 0 until array.length()) {
        val doc = Document.parse(array.getJSONObject(i).toString())
        doc.remove("_id")  // <-- eliminar _id para que MongoDB genere uno nuevo
        documentos.add(doc)
    }

    if (documentos.isEmpty()) {
        println("El archivo JSON está vacío")
        return
    }

    val db = cliente.getDatabase(NOM_BD)

    val nombreColeccion =coleccion.namespace.collectionName

    // Borrar colección si existe
    if (db.listCollectionNames().contains(nombreColeccion)) {
        db.getCollection(nombreColeccion).drop()
        println("Colección '$nombreColeccion' eliminada antes de importar.")
    }

    // Insertar documentos
    try {
        coleccion.insertMany(documentos)
        println("Importación completada: ${documentos.size} documentos de $nombreColeccion.")
    } catch (e: Exception) {
        println("Error importando documentos: ${e.message}")
    }
}



fun exportarBD(coleccion: MongoCollection<Document>, rutaJSON: String) {
    val settings = JsonWriterSettings.builder().indent(true).build()
    val file = File(rutaJSON)
    file.printWriter().use { out ->
        out.println("[")
        val cursor = coleccion.find().iterator()
        var first = true
        while (cursor.hasNext()) {
            if (!first) out.println(",")
            val doc = cursor.next()
            out.print(doc.toJson(settings))
            first = false
        }
        out.println("]")
        cursor.close()
    }

    println("Exportación de ${coleccion.namespace.collectionName} completada")
}







