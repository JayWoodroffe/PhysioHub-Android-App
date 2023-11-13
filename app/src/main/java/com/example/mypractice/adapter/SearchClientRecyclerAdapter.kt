package com.example.mypractice.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypractice.R
import com.example.mypractice.model.ClientModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SearchClientRecyclerAdapter(options: FirestoreRecyclerOptions<ClientModel>, private val context: Context) :
    FirestoreRecyclerAdapter<ClientModel, SearchClientRecyclerAdapter.ClientModelViewHolder>(options) {
    class ClientModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameText: TextView = itemView.findViewById(R.id.tvUserName)
        val subText: TextView = itemView.findViewById(R.id.tvUserSubText)
        val profilePic: ImageView = itemView.findViewById(R.id.ivProfilePicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.client_recyclerview, parent, false)
        return ClientModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientModelViewHolder, position: Int, model: ClientModel) {
        holder.usernameText.text = model.name
        holder.subText.text = model.number


    }
}