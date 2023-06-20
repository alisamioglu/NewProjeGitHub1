package com.samioglu.newc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import kotlin.math.min

class Aidat : AppCompatActivity() {

    private val PAYMENT_AMOUNT = 100L
    private val TOTAL_MONTHS = 12
    private val PAYMENT_INTERVAL = 120000L // 2 dakika (2 * 60 * 1000)

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var balanceTextView: TextView
    private lateinit var paymentButton: Button
    private lateinit var paymentInfoTextView: TextView
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidat)

        databaseRef = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        balanceTextView = findViewById(R.id.textView)
        paymentButton = findViewById(R.id.button3)
        paymentInfoTextView = findViewById(R.id.paymentInfoTextView)

        if (userId != null) {
            userRef = databaseRef.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Verileri al ve ayarla
                    retrieveAndSetData(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Hata durumuyla ilgili işlemler burada yapılabilir
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        // Verileri güncelle
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            userRef = databaseRef.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Verileri al ve ayarla
                    retrieveAndSetData(snapshot)

                    // Mesajı göster
                    showPaymentInfoMessage(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Hata durumuyla ilgili işlemler burada yapılabilir
                }
            })
        }
    }

    private fun retrieveAndSetData(snapshot: DataSnapshot) {
        // Verileri al ve ayarla
        val balance = snapshot.child("balance").getValue(Long::class.java)
        val lastPaymentTimestamp = snapshot.child("lastPaymentTimestamp").getValue(Long::class.java)
        val currentMonth = snapshot.child("currentMonth").getValue(Int::class.java)

        if (balance != null && lastPaymentTimestamp != null && currentMonth != null) {
            val currentTime = Calendar.getInstance().timeInMillis
            val elapsedTime = currentTime - lastPaymentTimestamp

            if (elapsedTime >= PAYMENT_INTERVAL) {
                // Ödeme yapılması gereken süre geçti, yeni ödeme yapabilir
                val remainingMonths = calculateRemainingMonths(currentTime, lastPaymentTimestamp)
                val totalPayment = remainingMonths * PAYMENT_AMOUNT
                val remainingBalance = balance - totalPayment

                userRef.child("balance").setValue(remainingBalance)
                userRef.child("lastPaymentTimestamp").setValue(currentTime)
                userRef.child("currentMonth").setValue(currentMonth + remainingMonths)

                balanceTextView.text = remainingBalance.toString()

                if (currentMonth == 0) {
                    showInitialPaymentMessage(totalPayment, remainingBalance)
                } else {
                    showPaymentSuccessMessage(remainingMonths, totalPayment, remainingBalance)
                }

                paymentButton.isEnabled = true
            } else {
                balanceTextView.text = balance.toString()
                paymentButton.isEnabled = false
                showPaymentWarningMessage(elapsedTime)
            }
        } else {
            val initialBalance = 1200L
            val initialLastPaymentTimestamp = Calendar.getInstance().timeInMillis
            val initialCurrentMonth = 0

            userRef.child("balance").setValue(initialBalance)
            userRef.child("lastPaymentTimestamp").setValue(initialLastPaymentTimestamp)
            userRef.child("currentMonth").setValue(initialCurrentMonth)

            balanceTextView.text = initialBalance.toString()
            paymentButton.isEnabled = false
            showInitialPaymentMessage(initialBalance, initialBalance)
        }
    }

    private fun calculateRemainingMonths(currentTime: Long, lastPaymentTimestamp: Long): Int {
        val elapsedTime = currentTime - lastPaymentTimestamp
        val remainingMonths = (elapsedTime / PAYMENT_INTERVAL).toInt()
        return min(remainingMonths, TOTAL_MONTHS)
    }

    private fun addPaymentInfoMessage(paymentNumber: Int) {
        val paymentInfo = "$paymentNumber. aidat ödemesi yapıldı."
        val currentText = paymentInfoTextView.text.toString()

        val updatedText = if (currentText.isNotEmpty()) {
            "$currentText\n$paymentInfo"
        } else {
            paymentInfo
        }

        paymentInfoTextView.text = updatedText
    }

    private fun showPaymentSuccessMessage(remainingMonths: Int, totalPayment: Long, remainingBalance: Long) {
        val message = "$remainingMonths adet aidat ödendi. Toplam ödeme miktarı: $totalPayment TL. Kalan bakiye: $remainingBalance TL."
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        addPaymentInfoMessage(remainingMonths) // Ödeme bilgi mesajını ekle
    }

    private fun showPaymentWarningMessage(elapsedTime: Long) {
        val minutesRemaining = (PAYMENT_INTERVAL - elapsedTime) / 60000
        val message = "Son ödemenizin üzerinden $minutesRemaining dakika geçmeden yeni ödeme yapamazsınız."
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showInitialPaymentMessage(totalPayment: Long, remainingBalance: Long) {
        val message = "İlk ödemenizi yapmanız gerekmektedir. Ödeme miktarı: $totalPayment TL. Kalan bakiye: $remainingBalance TL."
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showPaymentInfoMessage(snapshot: DataSnapshot) {
        val paymentNumber = snapshot.child("currentMonth").getValue(Int::class.java)
        if (paymentNumber != null) {
            addPaymentInfoMessage(paymentNumber)
        }
    }

    // Diğer yöntemler burada...
}


