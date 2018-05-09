package com.example.pinkienort.databaseapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {

    var idolId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnSaveIdol: Button = findViewById(R.id.btnSaveIdol)
        val lvIdols: ListView = findViewById(R.id.lvIdols)

        lvIdols.setOnItemClickListener(ListItemClickListener())

        btnSaveIdol.setOnClickListener(SaveButtonClickListener())

    }

    /**
     *  Event handler is called when the save button is clicked.
     */
    inner class SaveButtonClickListener : View.OnClickListener {

        override fun onClick(v: View?) {
            val helper = DatabaseHelper(this@MainActivity)
            val db = helper.writableDatabase

            run {
                val sqlDelete = "DELETE FROM idols WHERE id = ?"
                val stmt = db.compileStatement(sqlDelete)
                stmt.bindLong(1, idolId.toLong())
                stmt.executeUpdateDelete()
            }

            val etIdolProfile: EditText = findViewById(R.id.etIdolProfile)
            val idolProfile = etIdolProfile.text.toString()

            val tvIdolName: TextView = findViewById(R.id.tvIdolName)
            val idolName = tvIdolName.text

            run {
                val sqlInsert = "INSERT INTO idols (id, name, profile) VALUES (?, ?, ?)"
                val stmt = db.compileStatement(sqlInsert)
                stmt.bindLong(1, idolId.toLong())
                stmt.bindString(2, idolName.toString())
                stmt.bindString(3, idolProfile)
                stmt.executeInsert()
            }

            db.close()

            tvIdolName.text = getString(R.string.tvIdolName)
            etIdolProfile.setText("", TextView.BufferType.EDITABLE)
            val btnSaveIdol: Button = findViewById(R.id.btnSaveIdol)
            btnSaveIdol.isEnabled = false
        }
    }

    /**
     * Event listener for lvIdols list view.
     */
    inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            this@MainActivity.idolId = position
            val idolName = parent?.getItemAtPosition(position).toString()
            val tvIdolName: TextView = findViewById(R.id.tvIdolName)
            tvIdolName.setText(idolName, TextView.BufferType.EDITABLE)
            val btnSaveIdol: Button = findViewById(R.id.btnSaveIdol)
            btnSaveIdol.isEnabled = true

            val helper = DatabaseHelper(this@MainActivity)
            val db = helper.readableDatabase

            val sql = "SELECT * FROM idols WHERE id = ${this@MainActivity.idolId}"
            val cursor = db.rawQuery(sql, null)

            var profile = ""
            while(cursor.moveToNext()) {
                val index = cursor.getColumnIndex("profile")
                profile = cursor.getString(index)
            }

            val etProfile: EditText = findViewById(R.id.etIdolProfile)
            etProfile.setText(profile, TextView.BufferType.EDITABLE)

            db.close()

        }
    }
}
