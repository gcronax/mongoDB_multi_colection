import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import org.bson.Document
import java.util.InputMismatchException

fun menuClientes() {
    var itera = true
    do {
        println()
        println("   Selecciona una opcion: ")
        println("1. Mostrar Clientes")
        println("2. Insertar Cliente")
        println("3. Eliminar Cliente")
        println("4. Actualizar Cliente")
        println("5. Salir")
        try {
            val select: Int = isInt()
            when (select) {
                1 -> {
                    mostrarClientes()
                }
                2 -> {
                    insertarCliente()
                }
                3 -> {
                    eliminarCliente()
                }
                4 -> {
                    actualizarCliente()

                }
                5 -> {
                    itera = false
                }

                else -> {
                    println("Opcion no valida. Por favor, selecciona una opcion del 1 al 5.")
                }
            }

        } catch (e: InputMismatchException) {
            println("Error: Debes introducir un numero valido.")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    } while (itera)
}

fun mostrarClientes() {
    println();
    println("**** Listado de clientes:")
    coleccion.find().forEach { doc ->

        println(
            "[${doc.getInteger("id_cliente")}] " +
                    "nombre: ${doc.getString("nombre")}"
        )
    }
}

fun insertarCliente() {
    //conectar con la BD

    val coleccion = coleccion

    print("ID del cliente: ")
    val id_cliente = isInt()
    print("Nombre cliente: ")
    val nombre = isString()




    val doc = Document("id_cliente", id_cliente)
        .append("nombre", nombre)


    coleccion.insertOne(doc)
    println("Cliente insertado con ID: ${doc.getObjectId("_id")}")
}

fun actualizarCliente() {
    //conectar con la BD

    val coleccion = coleccion

    print("ID del cliente a modificar: ")
    val id_cliente = isInt()


    val cliente = coleccion.find(Filters.eq("id_cliente", id_cliente)).firstOrNull()
    if (cliente == null) {
        println("No se encontró ningun cliente con id_cliente = \"$id_cliente\".")
    }
    else {
        println(
            "cliente encontrado( nombre: ${cliente.getString("nombre")} )"
        )

        print("Nombre cliente a modificar: ")
        val nombre = isString()


        // Actualizar el documento
        val result = coleccion.updateMany(
            Filters.eq("id_cliente", id_cliente),
            Document("\$set",
                Document()
                    .append("nombre", nombre)
            ),

            )


        if (result.modifiedCount > 0)
            println("clientes actualizados correctamente (${result.modifiedCount} documento modificado).")
        else
            println("No se modificó ningún documento (help).")
    }

}

fun eliminarCliente() {
    //conectar con la BD

    val coleccion = coleccion

    print("ID del cliente a eliminar: ")
    val id_cliente = isInt()

    val result = coleccion.deleteOne(Filters.eq("id_cliente", id_cliente))
    if (result.deletedCount > 0)
        println("cliente eliminado correctamente.")
    else
        println("No se encontró ninguna cliente con ese ID.")

}


