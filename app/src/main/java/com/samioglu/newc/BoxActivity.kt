package com.samioglu.newc

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class BoxActivity : AppCompatActivity() {
    private val postManager = PostManager()
    private lateinit var postAdapter: ArrayAdapter<String>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_box)

        // Firebase veritabanı referansını alın ve "boxpost" düğümüne gönderin
        database = FirebaseDatabase.getInstance().reference.child("boxpost")

        val postList: ListView = findViewById(R.id.postList)
        postAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        postList.adapter = postAdapter

        updatePostList()

        val postInput: EditText = findViewById(R.id.postInput)
        findViewById<View>(R.id.postButton).setOnClickListener {
            val content = postInput.text.toString()
            postManager.addPost(content)

            // Firebase veritabanına yazıyı ekle
            val key = database.push().key
            key?.let {
                database.child(it).setValue(content)
            }

            postInput.text.clear()
            updatePostList()
        }
    }

    private fun updatePostList() {
        // Firebase veritabanından yazıları çek
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val postStrings = mutableListOf<String>()
                for (postSnapshot in dataSnapshot.children) {
                    val postContent = postSnapshot.getValue(String::class.java)
                    postContent?.let {
                        postStrings.add(it)
                    }
                }

                postAdapter.clear()
                postAdapter.addAll(postStrings)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}
