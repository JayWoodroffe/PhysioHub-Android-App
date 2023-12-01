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

    // onItemClick property
    var onItemClick: ((position: Int, clientId:String) -> Unit)? = null
    class ClientModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameText: TextView = itemView.findViewById(R.id.tvUserName)
        val subText: TextView = itemView.findViewById(R.id.tvUserSubText)
        val profilePic: ImageView = itemView.findViewById(R.id.ivProfilePicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientModelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.client_recyclerview, parent, false)
        val viewHolder = ClientModelViewHolder(view)

        // Set OnClickListener here
        view.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clientId = getItem(position).clientId // Assuming "id" is the field holding the client ID
                if (clientId != null) {
                    onItemClick?.invoke(position, clientId)
                }
            }

        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ClientModelViewHolder, position: Int, model: ClientModel) {
        val clientName = model.name
        val clientNumber = model.number
        val clientId = model.clientId ?: ""

        holder.usernameText.text = clientName
        holder.subText.text = clientNumber

        // Set the document ID as a tag
        holder.itemView.tag = mapOf(
            "clientId" to clientId,
            // Add other details as needed
        )


    }
}