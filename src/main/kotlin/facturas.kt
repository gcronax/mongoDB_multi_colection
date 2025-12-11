import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import org.bson.Document
import java.util.InputMismatchException

fun menuFacturas() {
    var itera = true
    do {
        println()
        println("   Selecciona una opcion: ")
        println("1. Mostrar Facturas")
        println("2. Insertar Factura")
        println("3. Eliminar Factura")
        println("4. Actualizar Factura")
        println("5. Mostrar Factura")
        println("6. Salir")
        try {
            val select: Int = isInt()
            when (select) {
                1 -> {
                    mostrarFacturas()
                }
                2 -> {
                    insertarFactura()
                }
                3 -> {
                    eliminarFactura()
                }
                4 -> {
                    actualizarFactura()

                }
                5 -> {
                    mostrarFactura()
                }
                6 -> {
                    itera = false
                }

                else -> {
                    println("Opcion no valida. Por favor, selecciona una opcion del 1 al 6.")
                }
            }

        } catch (e: InputMismatchException) {
            println("Error: Debes introducir un numero valido.")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    } while (itera)
}

fun mostrarFacturas() {
    println();
    println("**** Listado de facturas:")
    coleccionFacturas.find().forEach { doc ->
//        val id_factura = doc.getInteger("id_factura")
//        val modelo = doc.getString("modelo")
//        val marca = doc.getString("marca")
//        val consumo = doc.get("consumo").toString().toDouble()
//        val hp = doc.getInteger("hp")

        println(
            "[${doc.getInteger("id_factura")}] " +
                    "fecha: ${doc.getString("fecha")} " +
                    "id_coche: ${doc.getInteger("id_coche")} " +
                    "id_cliente: ${doc.getInteger("id_cliente")} " +
                    "precio: ${doc.getInteger("precio")} " +
                    "cantidad: ${doc.getInteger("cantidad")} "
        )
    }
}

fun insertarFactura() {
    //conectar con la BD

    val coleccionFacturas = coleccionFacturas

    print("ID de la factura: ")
    val id_factura = isInt()
    print("Fecha: ")
    val fecha = isString()
    print("ID del coche: ")
    val id_coche = isInt()
    print("ID del cliente: ")
    val id_cliente = isInt()
    print("precio: ")
    val precio = isInt()
    print("cantidad: ")
    val cantidad = isInt()



    val doc = Document("id_factura", id_factura)
        .append("fecha", fecha)
        .append("id_coche", id_coche)
        .append("id_cliente", id_cliente)
        .append("precio", precio)
        .append("cantidad", cantidad)


    coleccionFacturas.insertOne(doc)
    println("factura insertado con ID: ${doc.getObjectId("_id")}")
}

fun actualizarFactura() {
    //conectar con la BD

    val coleccionFacturas = coleccionFacturas

    print("ID del factura a modificar: ")
    val id_factura = isInt()


    val doc = coleccionFacturas.find(Filters.eq("id_factura", id_factura)).firstOrNull()
    if (doc == null) {
        println("No se encontró ningun factura con id_factura = \"$id_factura\".")
    }
    else {
        println(
            "[${doc.getInteger("id_factura")}] " +
                    "fecha: ${doc.getString("fecha")} " +
                    "id_coche: ${doc.getInteger("id_coche")} " +
                    "id_cliente: ${doc.getInteger("id_cliente")} " +
                    "precio: ${doc.getInteger("precio")} " +
                    "cantidad: ${doc.getInteger("cantidad")} "
        )

        print("ID de la factura a modificar: ")
        val id_factura = isInt()
        print("Fecha a modificar: ")
        val fecha = isString()
        print("ID del coche a modificar: ")
        val id_coche = isInt()
        print("ID del cliente a modificar: ")
        val id_cliente = isInt()
        print("precio a modificar: ")
        val precio = isInt()
        print("cantidad a modificar: ")
        val cantidad = isInt()

        // Actualizar el documento
        val result = coleccionFacturas.updateMany(
            Filters.eq("id_factura", id_factura),
            Document("\$set",
                Document()
                    .append("fecha", fecha)
                    .append("id_coche", id_coche)
                    .append("id_cliente", id_cliente)
                    .append("precio", precio)
                    .append("cantidad", cantidad)
            ),

            )


        if (result.modifiedCount > 0)
            println("facturas actualizados correctamente (${result.modifiedCount} documento modificado).")
        else
            println("No se modificó ningún documento (help).")
    }

}

fun eliminarFactura() {
    //conectar con la BD

    val coleccionFacturas = coleccionFacturas

    print("ID del factura a eliminar: ")
    val id_factura = isInt()

    val result = coleccionFacturas.deleteOne(Filters.eq("id_factura", id_factura))
    if (result.deletedCount > 0)
        println("factura eliminado correctamente.")
    else
        println("No se encontró ninguna factura con ese ID.")

}

fun mostrarFactura() {

    val coleccionFacturas = coleccionFacturas

    print("ID de la factura: ")
    val id_factura = isInt()

    val facturaDoc = coleccionFacturas
        .find(Document("id_factura", id_factura))
        .first()

    if (facturaDoc == null) {
        println("No existe ninguna factura con ID $id_factura")
        return
    }

    val fecha = facturaDoc["fecha"] as String

    val pipeline = listOf(
        Document("\$match", Document("id_factura", id_factura)),
        Document("\$lookup", Document()
            .append("from", "cars")
            .append("localField", "id_coche")
            .append("foreignField", "id_coche")
            .append("as", "coche")
        ),
        Document("\$unwind", "\$coche"),
        Document("\$project", Document()
            .append("marca", "\$coche.marca")
            .append("cantidad", 1)
            .append("precio", 1)
            .append("subtotal", Document("\$multiply", listOf("\$precio", "\$cantidad")))
        )
    )

    // Ejecutar la agregación para obtener la lista de líneas
    val lineas = coleccionFacturas.aggregate(pipeline).toList()

    if (lineas.isEmpty()) {
        println("No se encontraron líneas para la factura $id_factura")
        return
    }

    // Encabezado de la factura
    println("===============================================================")
    println("Factura ID: $id_factura")
    println("Fecha: $fecha")
    println("---------------------------------------------------------------")
    println(String.format("%-15s %-10s %-10s %-12s", "Coche", "Cantidad", "Precio", "Subtotal"))
    println("---------------------------------------------------------------")

    var totalFactura = 0.0

    // Iterar sobre las líneas de la factura
    lineas.forEach { linea ->
        val nombre = linea["marca"] as String
        val cantidad = linea["cantidad"] as Int
        val precio = linea["precio"] as Int
        val subtotal = (linea["subtotal"] as Number).toDouble()

        totalFactura += subtotal

        println(String.format("%-15s %-10d %-10s %-12s",
            nombre, cantidad, precio, subtotal
        ))
    }

    var totalIVA =totalFactura*0.21

    // Mostrar pie de factura con totales
    println("---------------------------------------------------------------")
    println(String.format("%-15s %-10s %-10s %-12s", "", "TOTAL:", totalFactura, ""))
    println(String.format("%-15s %-10s %-10s %-12s", "", "IVA 21%:", totalIVA, ""))
    println(String.format("%-15s %-10s %-10s %-12s", "", "TOTAL CON IVA:", totalFactura + totalIVA, ""))
    println("===============================================================")
}