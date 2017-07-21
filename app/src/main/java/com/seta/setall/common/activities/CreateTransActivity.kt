package com.seta.setall.common.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.seta.setall.R
import org.jetbrains.anko.startActivity
import java.util.*

class CreateTransActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trans)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnAddApp -> startActivity<OwnedGamesActivity>()
            R.id.mTvDate -> {
                val calendar = Calendar.getInstance()
                val datePickDialog = DatePickerDialog(this,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
            }
        }
    }
}
