package mx.tecnm.tepic.u3_practica1_sqliteconfirestore

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {

    var baseSQLite=BaseDatos(this,"prueba", null,1)
    var idSelec=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var extra =intent.extras
        idSelec=extra!!.getString("idSeleccionado")!!

        button11.setOnClickListener {
            actualizar()
        }

        button22.setOnClickListener {
            finish()
        }
    }

    private fun actualizar() {
        try{
            var transaccion = baseSQLite.writableDatabase

            if(nombre2.getText().toString()=="" || producto2.getText().toString()=="" || precio2.getText().toString()==""){
                mensaje("FAVOR DE INGRESAR INFORMACION")
            }else{
                var SQL="UPDATE APARTADO SET NOMBRECLIENTE='${nombre2.text.toString()}',PRODUCTO='${producto2.text.toString()}',PRECIO='${precio2.text.toString()}' WHERE IDAPARTADO=${idSelec}"
                transaccion.execSQL(SQL)
                MainActivity().cargarLista()
                transaccion.close()
                finish()
            }

        }catch(err: SQLiteException){
            mensaje(err.message!!)
        }
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){_,_->}
            .show()
    }
}