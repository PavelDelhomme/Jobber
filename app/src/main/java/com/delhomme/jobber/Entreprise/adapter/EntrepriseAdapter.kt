import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.R

class EntrepriseAdapter(
    private var entreprises: List<Entreprise>,
    private val itemClickListener: (Entreprise) -> Unit,
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (String) -> Unit
) : RecyclerView.Adapter<EntrepriseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvEntrepriseName: TextView = view.findViewById(R.id.tvEntrepriseName)
        private val btnEdit: ImageButton = view.findViewById(R.id.btnEditEntreprise)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteEntreprise)

        fun bind(
            entreprise: Entreprise,
            clickListener: (Entreprise) -> Unit,
            deleteListener: (String) -> Unit,
            editListener: (String) -> Unit
        ) {
            tvEntrepriseName.text = entreprise.nom
            itemView.setOnClickListener { clickListener(entreprise) }
            btnEdit.setOnClickListener { editListener(entreprise.nom) }
            btnDelete.setOnClickListener { deleteListener(entreprise.nom) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_entreprise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            entreprises[position],
            itemClickListener,
            deleteClickListener,
            editClickListener
        )
    }

    override fun getItemCount(): Int = entreprises.size

    fun updateEntreprises(newEntreprises: List<Entreprise>) {
        entreprises = newEntreprises
        notifyDataSetChanged()
    }
}