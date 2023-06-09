package com.samioglu.newc

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

class Aidat : AppCompatActivity() {

    private val PAYMENT_AMOUNT = 100L
    private val TOTAL_MONTHS = 12
    private val PAYMENT_INTERVAL = 120000L // 2 dakika (2 * 60 * 1000)

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var balanceTextView: TextView
    private lateinit var paymentButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidat)

        databaseRef = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        balanceTextView = findViewById(R.id.textView)
        paymentButton = findViewById(R.id.button3)

        if (userId != null) {
            val userRef = databaseRef.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val balance = snapshot.child("balance").getValue(Long::class.java)
                    val lastPaymentTimestamp = snapshot.child("lastPaymentTimestamp").getValue(Long::class.java)
                    val currentMonth = snapshot.child("currentMonth").getValue(Int::class.java)

                    if (balance != null && lastPaymentTimestamp != null && currentMonth != null) {
                        val currentTime = Calendar.getInstance().timeInMillis
                        val elapsedTime = currentTime - lastPaymentTimestamp

                        if (elapsedTime >= PAYMENT_INTERVAL) {
                            // Ödeme yapılması gereken süre geçti, yeni ödeme yapabilir
                            balanceTextView.text = balance.toString()
                            paymentButton.isEnabled = true
                            paymentButton.setOnClickListener {
                                makePayment(userRef, balance, lastPaymentTimestamp, currentMonth)
                            }
                        } else {
                            // Henüz ödeme yapılamaz, uyarı mesajını göster
                            balanceTextView.text = balance.toString()
                            paymentButton.isEnabled = false
                            showPaymentWarningMessage(elapsedTime)
                        }
                    } else {
                        // Kullanıcının bakiyesi, son ödeme zaman damgası veya mevcut ay henüz tanımlanmamış
                        val initialBalance = 1200L
                        val initialLastPaymentTimestamp = 0L
                        val initialCurrentMonth = 0

                        userRef.child("balance").setValue(initialBalance)
                        userRef.child("lastPaymentTimestamp").setValue(initialLastPaymentTimestamp)
                        userRef.child("currentMonth").setValue(initialCurrentMonth)

                        balanceTextView.text = initialBalance.toString()
                        paymentButton.isEnabled = false
                        showInitialPaymentMessage()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Hata durumunda yapılacak işlemler
                }
            })
        }
    }

    private fun makePayment(userRef: DatabaseReference, balance: Long, lastPaymentTimestamp: Long, currentMonth: Int) {
        val remainingBalance = balance - PAYMENT_AMOUNT

        if (currentMonth < TOTAL_MONTHS) {
            val currentTime = Calendar.getInstance().timeInMillis

            if (currentTime - lastPaymentTimestamp >= PAYMENT_INTERVAL) {
                // Ödeme yapılması gereken süre geçti, yeni ödeme yapabilir
                userRef.child("balance").setValue(remainingBalance)
                userRef.child("lastPaymentTimestamp").setValue(currentTime)
                userRef.child("currentMonth").setValue(currentMonth + 1)

                balanceTextView.text = remainingBalance.toString()

                // Ödeme mesajını göster
                showPaymentSuccessMessage(currentMonth + 1, remainingBalance)

                // Ödeme yapıldıktan sonra butonu etkinleştir
                paymentButton.isEnabled = true
            } else {
                // Son ödemenin üzerinden yeterli süre geçmedi, uyarı mesajını göster
                showNextPaymentWarningMessage()
            }
        } else {
            // Tüm aidat ödemeleri tamamlandı
            showPaymentCompleteMessage()

            // Aidat ödemeleri tamamlandığı için butonu etkisiz hale getir
            paymentButton.isEnabled = false
        }
    }

    private fun showPaymentSuccessMessage(currentMonth: Int, remainingBalance: Long) {
        val message = "Ödeme başarılı! ${getMonthName(currentMonth)} ayı için 100 TL ödendi. Kalan bakiye: $remainingBalance TL."
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showPaymentCompleteMessage() {
        val message = "Tüm aidat ödemeleri tamamlandı. Ödemeleri başarıyla tamamladınız."
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showPaymentWarningMessage(elapsedTime: Long) {
        val minutesRemaining = (PAYMENT_INTERVAL - elapsedTime) / 60000
        val message = "Son ödemenizin üzerinden $minutesRemaining dakika geçmeden yeni ödeme yapamazsınız."
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showNextPaymentWarningMessage() {
        val message = "Sonraki ödeme için 2 dakika beklemelisiniz."
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showInitialPaymentMessage() {
        val message = "İlk ödemenizi yapmanız gerekmektedir."
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getMonthName(month: Int): String {
        val months = arrayOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")
        return months[month - 1]
    }
}