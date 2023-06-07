package com.samioglu.newc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.samioglu.newc.databinding.ActivityHomePageBinding

class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var listView: ListView
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        listView = findViewById(R.id.listView)

        val items = arrayOf("Aidat Ödeme", "Sipariş Ver", "Duyurular")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        //yönlenirme


        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]

            // Seçilen öğeye göre yeni bir sayfaya geçiş yapabilirsiniz.
            when (selectedItem) {
                "Aidat Ödeme" -> {
                    val intent = Intent(this, Aidat::class.java)
                    startActivity(intent)
                }
                "Sipariş Ver" -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                "Duyurular" -> {
                    val intent = Intent(this, Duyuru::class.java)
                    startActivity(intent)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {
            auth.signOut()
            val intent = Intent(this,LogIn::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}