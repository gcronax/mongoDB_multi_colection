import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import org.bson.Document
import java.util.InputMismatchException

fun menuCoches() {
    var itera = true
    do {
        println()
        println("   Selecciona una opcion: ")
        println("1. Mostrar Coches")
        println("2. Insertar Coche")
        println("3. Eliminar Coche")
        println("4. Actualizar Coche")
        println("5. Varias operaciones")
        println("6. Salir")
        try {
            val select: Int = isInt()
            when (select) {
                1 -> {
                    mostrarCoches()
                }
                2 -> {
                    insertarCoche()
                }
                3 -> {
                    eliminarCoche()
                }
                4 -> {
                    actualizarCoche()

                }
                5 -> {
                    variasOperaciones()
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

fun mostrarCoches() {
    println();
    println("**** Listado de coches:")
    coleccionCoches.find().forEach { doc ->
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

fun insertarCoche() {
    //conectar con la BD

    val coleccionCoches = coleccionCoches

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


    coleccionCoches.insertOne(doc)
    println("Coche insertado con ID: ${doc.getObjectId("_id")}")
}

fun actualizarCoche() {
    //conectar con la BD

    val coleccionCoches = coleccionCoches

    print("ID del coche a modificar: ")
    val id_coche = isInt()


    val coche = coleccionCoches.find(Filters.eq("id_coche", id_coche)).firstOrNull()
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
        val result = coleccionCoches.updateMany(
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

fun eliminarCoche() {
    //conectar con la BD

    val coleccionCoches = coleccionCoches

    print("ID del coche a eliminar: ")
    val id_coche = isInt()

    val result = coleccionCoches.deleteOne(Filters.eq("id_coche", id_coche))
    if (result.deletedCount > 0)
        println("Coche eliminado correctamente.")
    else
        println("No se encontró ninguna coche con ese ID.")

}

fun variasOperaciones() {
    val col = coleccionCoches

    println("*****Coches que que tienen más de 200 de potencia")
    // 1) Filtro: altura > 100
    col.find(Filters.gt("hp", 200)).forEach { println(it.toJson()) }

    println("*****Marca y nombre común de todos los coches")
    // 2) Proyección: solo nombre_comun
    col.find().projection(Projections.include("marca","id_coche")).forEach { println(it.toJson()) }

    println("*****Potencia media de todos los coches")
    // 3) Agregación: media de altura
    val pipeline = listOf(
        Document(
            "\$group", Document("_id", null)
                .append("potenciaMedia", Document("\$avg", "\$hp"))
//            .append("\$sort", Document("potenciaMedia", -1))
        )
    )
    val aggCursor = col.aggregate(pipeline).iterator()
    aggCursor.use {
        while (it.hasNext()) println(it.next().toJson())
    }

}
