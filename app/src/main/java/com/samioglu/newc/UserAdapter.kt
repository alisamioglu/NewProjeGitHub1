import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.samioglu.newc.ChatActivity
import com.samioglu.newc.R

class UserAdapter(val context: Context, val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.textName.text = currentUser.name

        // Apartman Adı'nı göster
       // holder.apartmanAdiTextView.text = "Apartman Adı: ${currentUser.apartmanAdi}"

        // Bildirim simgesini gizle veya göster
        if (currentUser.hasUnreadMessages()) {
            holder.notificationIndicator.visibility = View.VISIBLE
            holder.counterTextView.visibility = View.VISIBLE
            holder.counterTextView.text = currentUser.unreadMessageCount.toString()
        } else {
            holder.notificationIndicator.visibility = View.GONE
            holder.counterTextView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)

            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("lastReadMessageId", currentUser.lastReadMessageId)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        // val apartmanAdiTextView = itemView.findViewById<TextView>(R.id.txt_apartman_adi_home)
        val notificationIndicator = itemView.findViewById<ImageView>(R.id.notificationIndicator)
        val counterTextView = itemView.findViewById<TextView>(R.id.counterTextView)
    }
}
