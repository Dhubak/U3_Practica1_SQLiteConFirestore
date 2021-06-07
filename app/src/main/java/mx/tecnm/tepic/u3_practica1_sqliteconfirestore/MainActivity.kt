package mx.tecnm.tepic.u3_practica1_sqliteconfirestore

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main3.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var baseSQLite=BaseDatos(this,"prueba", null,1)
    var listaID=ArrayList<String>()
    var listaID2=ArrayList<String>()
    var baseRemota= FirebaseFirestore.getInstance()
    var cadena= ""
    var datalista=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1.setOnClickListener{
            insertar()
        }

        button2.setOnClickListener{
            sincronizar()

        }

        button3.setOnClickListener{
            finish()

        }

        cargarLista()

    }


    fun borradoCompleto() {
            var eliminaT = baseSQLite.writableDatabase
            var SQL="DELETE FROM APARTADO"

            eliminaT.execSQL(SQL)
            eliminaT.close()
    }

    private fun sincronizar() {

        datalista.clear()
        baseRemota.collection("apartado")
            .addSnapshotListener {querySnapshot, firebaseFirestoreException ->

                if(firebaseFirestoreException !=null) {
                    mensaje("NO SE PUDO REALIZAR CONEXION CON LA NUBE")
                    return@addSnapshotListener
                }

                for(registro in querySnapshot!!)
                {
                    cadena=registro.id
                    datalista.add(cadena)
                }

                try {
                    var c = baseSQLite.readableDatabase
                    var res = c.query("APARTADO", arrayOf("*"),null,null,null,null,null)

                    if(res.moveToFirst())
                    {
                        do {
                            if(datalista.contains(res.getString(0))) {
                                baseRemota.collection("apartado")
                                    .document(res.getString(0))
                                    .update( "NOMBRECLIENTE",res.getString(1), "PRODUCTO",res.getString(2), "PRECIO", res.getString(3))
                                    .addOnFailureListener {
                                        androidx.appcompat.app.AlertDialog.Builder(this)
                                            .setTitle("ERROR")
                                            .setMessage("NO SE PUDO SUBIR\n${it.message!!}")
                                            .setPositiveButton("OK"){_,_->}
                                            .show()
                                    }
                            } else {

                                var datosInsertar = hashMapOf(
                                    "IDAPARTADO" to res.getString(0),
                                    "NOMBRECLIENTE" to res.getString(1),
                                    "PRODUCTO" to res.getString(2),
                                    "PRECIO" to res.getString(3).toFloat()
                                )

                                vaciarlista()

                                baseRemota.collection("apartado").document("${res.getString(0)}")
                                    .set(datosInsertar as Any)
                                    .addOnFailureListener{
                                        mensaje("NO SE PUDO SUBIR A LA NUBE\n${it.message!!}")
                                    }

                            }

                        }while(res.moveToNext())
                    } else {
                        datalista.add("NO SE HAN ENCONTRADO CAMBIOS QUE SUBIR")
                    }
                    c.close()
                } catch (e: SQLiteException) {
                    mensaje("ALGO SALIÓ MAL: " + e.message!!)
                }
            }

        alerta("LA SINCRONIZACION SE LLEVÓ A CABO SATISFACTORIAMENTE. TU INFOMACION YA SE ENCUENTRA EN LA NUBE")
        startActivity(Intent(this,MainActivity3::class.java))
    }


    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }

fun vaciarlista() {
        listaID2.clear()
        lista1.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listaID2)
        borradoCompleto()
    }


    fun cargarLista() {
        try{
            var select = baseSQLite.readableDatabase
            var clientes = ArrayList<String>()
            var SQL="SELECT * FROM APARTADO"
            var cursor = select.rawQuery(SQL,null)
            listaID.clear()

            if(cursor.moveToFirst()){

                do{
                    var data = cursor.getString(0)+" - "+cursor.getString(1)+" - "+ cursor.getString(2)+" - "+ cursor.getString(3)
                    clientes.add(data)
                    listaID.add(cursor.getInt(0).toString())
                }while(cursor.moveToNext())

            }else{
                clientes.add("AUN NO HAY INFORMACION")
            }
            select.close()
            lista1.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,clientes)
            lista1.setOnItemClickListener { _, _, pos, _ ->

                var idSelec = listaID.get(pos)

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("ATENCION")
                    .setMessage("QUE DESEAS HACER CON LA INFORMACION DEL ID: ${idSelec}")
                    .setNegativeButton("CANCELAR") { _, _ -> }
                    .setPositiveButton("ELIMINAR") { _, _ ->
                        eliminar(idSelec)
                    }

                    .setNeutralButton("ACTUALIZAR"){_,_->
                        var intent=Intent(this,MainActivity2::class.java)
                        intent.putExtra("idSeleccionado", idSelec)
                        startActivity(intent)
                    }
                    .show()
            }

        }catch(err:SQLiteException){
            mensaje(err.message!!)
        }
    }


    private fun eliminar(idBorrar: String) {
        try{
            var elimin = baseSQLite.writableDatabase
            var SQL="DELETE FROM APARTADO WHERE IDAPARTADO = ${idBorrar}"

            elimin.execSQL(SQL)
            cargarLista()
            elimin.close()

        }catch(err:SQLiteException){
            mensaje(err.message!!)
        }

    }


    fun insertar() {
        try{
            var inser = baseSQLite.writableDatabase

            if(nombre.getText().toString()=="" || producto.getText().toString()=="" || precio.getText().toString()==""){
                mensaje("FAVOR DE INGRESAR INFORMACION")
            }else{
                var SQL="INSERT INTO APARTADO VALUES(NULL, '${nombre.text.toString()}', '${producto.text.toString()}','${precio.text.toString()}')"

                inser.execSQL(SQL)
                mensaje("INFORMACION INSERTADA CORRECTAMENTE")
                cargarLista()
                limpiarCampos()
            }
            inser.close()

        }catch(err:SQLiteException){
            mensaje(err.message!!)
        }
    }


    fun limpiarCampos(){
        nombre.setText("")
        producto.setText("")
        precio.setText("")
    }

    fun mensaje(m:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(m)
            .setPositiveButton("OK") {_, _ -> }
            .show()
    }

}