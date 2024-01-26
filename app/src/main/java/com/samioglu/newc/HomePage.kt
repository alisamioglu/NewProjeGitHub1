package com.samioglu.newc

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.samioglu.newc.databinding.ActivityHomePageBinding

class HomePage : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var listView: ListView
    private lateinit var txtApartmanAdi: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        listView = findViewById(R.id.listView)
        txtApartmanAdi = findViewById(R.id.txt_apartman_adi_home)

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("user/$currentUserUid/apartmanAdi")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val apartmanAdi = snapshot.getValue(String::class.java)

                // Eğer apartmanAdi null ise, varsayılan bir değer kullanabilirsiniz
                val displayedApartmanAdi = apartmanAdi ?: "Varsayılan Apartman Adı"

                txtApartmanAdi.text = " $displayedApartmanAdi Apartmanına  HOŞGELDİNİZ"
            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumunda yapılacak işlemler
            }
        })

        // Sadece aynı apartmanda bulunan kullanıcıları listelemek için bir filtreleme yapacağız
        val currentUserApartment = databaseReference.toString()  // Apartman adını al
        val items = arrayOf("Aidat Ödeme", "Sipariş Ver", "Duyurular", "Dilek ve Şikayet")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]

            when (selectedItem) {
                "Aidat Ödeme" -> {
                    val intent = Intent(this, Aidat::class.java)
                    startActivity(intent)
                }
                "Sipariş Ver" -> {
                    // Sipariş ver ekranında sadece aynı apartmanda bulunan kullanıcıları göstermek için
                    // Intent'e aynı apartman adını ekleyerek gönderiyoruz
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("currentUserApartment", currentUserApartment)
                    startActivity(intent)
                }
                "Duyurular" -> {
                    val intent = Intent(this, Duyuru::class.java)
                    startActivity(intent)
                }
                "Dilek ve Şikayet" -> {
                    val intent = Intent(this, BoxActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            auth.signOut()
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
