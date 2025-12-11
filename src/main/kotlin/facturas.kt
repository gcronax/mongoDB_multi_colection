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
        println("5. Varias operaciones")
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
    coleccion.find().forEach { doc ->
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

    val coleccion = coleccion

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


    coleccion.insertOne(doc)
    println("factura insertado con ID: ${doc.getObjectId("_id")}")
}

fun actualizarFactura() {
    //conectar con la BD

    val coleccion = coleccion

    print("ID del factura a modificar: ")
    val id_factura = isInt()


    val doc = coleccion.find(Filters.eq("id_factura", id_factura)).firstOrNull()
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
        val result = coleccion.updateMany(
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

    val coleccion = coleccion

    print("ID del factura a eliminar: ")
    val id_factura = isInt()

    val result = coleccion.deleteOne(Filters.eq("id_factura", id_factura))
    if (result.deletedCount > 0)
        println("factura eliminado correctamente.")
    else
        println("No se encontró ninguna factura con ese ID.")

}

