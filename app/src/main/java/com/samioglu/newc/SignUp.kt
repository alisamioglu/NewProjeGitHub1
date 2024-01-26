package com.samioglu.newc

import User
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtApartmanAdi: EditText  // Yeni eklenen alan
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        edtName = findViewById(R.id.userName)
        edtEmail = findViewById(R.id.userEmailText)
        edtPassword = findViewById(R.id.passwordText)
        edtApartmanAdi = findViewById(R.id.apartmanAdiText)  // Yeni eklenen alan
        btnSignUp = findViewById(R.id.button2)

        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val apartmanAdi = edtApartmanAdi.text.toString()  // Yeni eklenen alanın değeri

            signUp(name, email, password, apartmanAdi)
        }
    }

    private fun signUp(name: String, email: String, password: String, apartmanAdi: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!, apartmanAdi)

                    val intent = Intent(this@SignUp, HomePage::class.java)
                    finish()
                    startActivity(intent)

                } else {
                    Toast.makeText(this@SignUp, "Some Error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, apartmanAdi: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()

        // "users" koleksiyonu altında kullanıcı bilgilerini ekleyin
        val user = User(name, email, uid, null, apartmanAdi) // lastReadMessageId'i null olarak bıraktım, çünkü bu aşamada bilinmiyor.
        mDbRef.child("user").child(uid).setValue(user)
    }

}
