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
    println("**** Listado de coches:")
    coleccion.find().forEach { doc ->
//        val id_coche = doc.getInteger("id_coche")
//        val modelo = doc.getString("modelo")
//        val marca = doc.getString("marca")
//        val consumo = doc.get("consumo").toString().toDouble()
//        val hp = doc.getInteger("hp")

        println(
            "[${doc.getInteger("id_coche")}] " +
                    "modelo: ${doc.getString("modelo")} " +
                    "marca: ${doc.getString("marca")} " +
                    "consumo: ${doc.get("consumo").toString().toDouble()} " +
                    "hp: ${doc.getInteger("hp")} "
        )
    }
}

fun insertarFactura() {
    //conectar con la BD

    val coleccion = coleccion

    print("ID del coche: ")
    val id_coche = isInt()
    print("Nombre modelo: ")
    val modelo = isString()
    print("Nombre marca: ")
    val marca = isString()
    print("Consumo: ")
    val consumo = isDouble()
    print("Potencia: ")
    val hp = isInt()



    val doc = Document("id_coche", id_coche)
        .append("modelo", modelo)
        .append("marca", marca)
        .append("consumo", consumo)
        .append("hp", hp)


    coleccion.insertOne(doc)
    println("Coche insertado con ID: ${doc.getObjectId("_id")}")
}

fun actualizarFactura() {
    //conectar con la BD

    val coleccion = coleccion

    print("ID del coche a modificar: ")
    val id_coche = isInt()


    val coche = coleccion.find(Filters.eq("id_coche", id_coche)).firstOrNull()
    if (coche == null) {
        println("No se encontró ningun coche con id_coche = \"$id_coche\".")
    }
    else {
        println(
            "Coche encontrado( " +
                    "modelo: ${coche.getString("modelo")} " +
                    "marca: ${coche.getString("marca")} " +
                    "consumo: ${coche.get("consumo").toString().toDouble()} " +
                    "hp: ${coche.getInteger("hp")} )"
        )

        print("Nombre modelo a modificar: ")
        val modelo = isString()
        print("Nombre marca a modificar: ")
        val marca = isString()
        print("Consumo a modificar: ")
        val consumo = isDouble()
        print("Potencia a modificar: ")
        val hp = isInt()

        // Actualizar el documento
        val result = coleccion.updateMany(
            Filters.eq("id_coche", id_coche),
            Document("\$set",
                Document()
                    .append("modelo", modelo)
                    .append("marca", marca)
                    .append("consumo", consumo)
                    .append("hp", hp)
            ),

            )


        if (result.modifiedCount > 0)
            println("Coches actualizados correctamente (${result.modifiedCount} documento modificado).")
        else
            println("No se modificó ningún documento (help).")
    }

}

fun eliminarFactura() {
    //conectar con la BD

    val coleccion = coleccion

    print("ID del coche a eliminar: ")
    val id_coche = isInt()

    val result = coleccion.deleteOne(Filters.eq("id_coche", id_coche))
    if (result.deletedCount > 0)
        println("Coche eliminado correctamente.")
    else
        println("No se encontró ninguna coche con ese ID.")

}

