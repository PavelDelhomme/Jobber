package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Relance
import java.text.SimpleDateFormat
import java.util.Locale

class RelanceAdapter(
    private var relances: List<Relance>,
    private val dataRepository: DataRepository,
    private val itemClickListener: (Relance) -> Unit,
    private val deleteClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<RelanceAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateRelance: TextView = view.findViewById(R.id.dateRelance)
        private val entreprise: TextView = view.findViewById(R.id.entrepriseRelance)
        private val candidatureTitre: TextView = view.findViewById(R.id.candidatureTitre)
        private val plateformeUtilise: TextView = view.findViewById(R.id.plateformeRelance)
        private val notesRelance: TextView = view.findViewById(R.id.notesRelance)
        private val deleteButton: Button = view.findViewById(R.id.btnDeleteRelance)

        fun bind(relance: Relance, dataRepository: DataRepository ,clickListener: (Relance) -> Unit, deleteListener: (String) -> Unit) {
            val entrepriseName = dataRepository.getEntrepriseById(relance.entrepriseId)?.nom ?: "Entreprise inconnue"
            val candidature = dataRepository.getCandidatureById(relance.candidatureId)?.titre_offre ?: "Offre inconnue"
            dateRelance.text = SimpleDateFormat("dd/MM/yyyyy", Locale.getDefault()).format(relance.date_relance)
            entreprise.text = entrepriseName
            candidatureTitre.text = candidature
            plateformeUtilise.text = relance.plateformeUtilisee
            notesRelance.text = relance.notes ?: "Aucune notes de relances"
            itemView.setOnClickListener { clickListener(relance) }
            deleteButton.setOnClickListener { deleteListener(relance.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_relance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(relances[position], dataRepository, itemClickListener, deleteClickListener)
    }

    override fun getItemCount() = relances.size

    fun updateRelances(newRelances: List<Relance>) {
        relances = newRelances
        notifyDataSetChanged()
    }
}