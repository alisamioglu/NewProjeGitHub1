package com.samioglu.newc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager


import com.samioglu.newc.databinding.ActivityDuyuruBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Duyuru : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var binding : ActivityDuyuruBinding


    val postArrayList : ArrayList<Post> = ArrayList()
    var adapter : FeedRecyclerAdapter? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.duyurr_menu,menu)


        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.add_post) {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDuyuruBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()


        //recyclerview

        binding.NoticeBoard.layoutManager = LinearLayoutManager(this)

        adapter = FeedRecyclerAdapter(postArrayList)
        binding.NoticeBoard.adapter = adapter

    }


    fun getDataFromFirestore() {

        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            } else {

                if (snapshot != null) {
                    if (!snapshot.isEmpty) {

                        postArrayList.clear()

                        val documents = snapshot.documents
                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val useremail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post = Post(useremail,comment, downloadUrl)
                            postArrayList.add(post)
                        }
                        adapter!!.notifyDataSetChanged()

                    }
                }

            }
        }

    }
}