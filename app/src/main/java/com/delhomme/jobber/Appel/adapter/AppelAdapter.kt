package com.delhomme.jobber.Appel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class AppelAdapter(
    var appels: List<Appel>,
    private var dataRepository: DataRepository,
    private val itemClickListener: (Appel) -> Unit,
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (String) -> Unit
) : RecyclerView.Adapter<AppelAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewDate: TextView = view.findViewById(R.id.tvDateAppel)
        private val textViewObjet: TextView = view.findViewById(R.id.tvObjetAppel)
        private val textViewContact: TextView = view.findViewById(R.id.tvContactAppel)
        private val textViewNotes: TextView = view.findViewById(R.id.tvNotesAppels)
        private val textViewEntreprise: TextView = view.findViewById(R.id.tvEntrepriseAppel)

        fun bind(appel: Appel, dataRepository: DataRepository, clickListener: (Appel) -> Unit, deleteListener: (String) -> Unit, editListener: (String) -> Unit) {
            val contactName = appel.contact_id?.let { dataRepository.getContactById(it)?.getFullName() } ?: "No Contact"
            textViewDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(appel.date_appel)
            textViewObjet.text = appel.objet
            textViewContact.text = contactName
            textViewNotes.text = appel.notes
            textViewEntreprise.text = appel.entrepriseNom
            itemView.setOnClickListener { clickListener(appel) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appels[position], dataRepository, itemClickListener, deleteClickListener, editClickListener)
    }

    override fun getItemCount() = appels.size

    fun updateAppels(newList: List<Appel>) {
        appels = newList
        notifyDataSetChanged()
    }
}
