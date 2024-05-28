package com.delhomme.jobber.Activity.Candidature

import com.delhomme.jobber.Adapter.RelanceAdapter
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Activity.Appel.AddAppelActivity
import com.delhomme.jobber.Activity.Appel.DetailsAppelActivity
import com.delhomme.jobber.Activity.Appel.EditAppelActivity
import com.delhomme.jobber.Adapter.AppelAdapter
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Utils.CandidatureState
import com.delhomme.jobber.Activity.Contact.AddContactActivity
import com.delhomme.jobber.Activity.Contact.DetailsContactActivity
import com.delhomme.jobber.Activity.Contact.EditContactActivity
import com.delhomme.jobber.Adapter.ContactAdapter
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.Activity.Entretien.AddEntretienActivity
import com.delhomme.jobber.Activity.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Activity.Entretien.EditEntretienActivity
import com.delhomme.jobber.Adapter.EntretienAdapter
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import com.delhomme.jobber.Activity.Relance.AddRelanceActivity
import com.delhomme.jobber.Activity.Relance.DetailsRelanceActivity
import com.delhomme.jobber.Activity.Relance.EditRelanceActivity
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Model.Relance
import java.util.Locale

class DetailsCandidatureActivity : AppCompatActivity() {
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var entretienDataRepository: EntretienDataRepository
    private lateinit var relanceDataRepository: RelanceDataRepository

    private var candidatureId: String? = null
    private lateinit var candidature: Candidature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_candidature)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRepositories()

        candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        candidature = candidatureDataRepository.getCandidatureById(candidatureId!!) ?: run {
            Toast.makeText(this, "Candidature non trouvée.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        displayCandidatureDetails()
    }

    private fun initRepositories() {
        candidatureDataRepository = CandidatureDataRepository(applicationContext)
        contactDataRepository = ContactDataRepository(applicationContext)
        appelDataRepository = AppelDataRepository(applicationContext)
        entretienDataRepository = EntretienDataRepository(applicationContext)
        relanceDataRepository = RelanceDataRepository(applicationContext)
    }

    private fun setupUI() {
        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
            })
        }

        findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            startActivity(Intent(this, AddAppelActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
                putExtra("CANDIDATURE_ID", candidature.id)
            })
        }

        findViewById<Button>(R.id.btnAddRelance).setOnClickListener {
            startActivity(Intent(this, AddRelanceActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
                putExtra("CANDIDATURE_ID", candidature.id)
            })
        }

        findViewById<Button>(R.id.btnAddEntretien).setOnClickListener {
            startActivity(Intent(this, AddEntretienActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
                putExtra("CANDIDATURE8ID", candidature.id)
            })
        }

        findViewById<ImageButton>(R.id.btnEditCandidature).setOnClickListener {
            startActivity(Intent(this, EditCandidatureActivity::class.java).apply {
                putExtra("CANDIDATURE_ID", candidatureId)
            })
        }

        findViewById<ImageButton>(R.id.button_mark_as_rejected).setOnClickListener {
            markAsRejected()
        }

        findViewById<ImageButton>(R.id.button_mark_as_accepted).setOnClickListener {
            markAsAccepted()
        }

        setupRecyclerView()
    }

    private fun displayCandidatureDetails() {
        findViewById<TextView>(R.id.titreoffre).text = candidature.titre_offre
        findViewById<TextView>(R.id.tvEntrepriseCandidature).text = candidature.entrepriseNom
        findViewById<TextView>(R.id.tvNotesCandidature).text = candidature.notes
        findViewById<TextView>(R.id.tvDateCandidature).text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(candidature.date_candidature)
        findViewById<TextView>(R.id.tvTypePoste).text = candidature.type_poste
        findViewById<TextView>(R.id.tvPlateforme).text = candidature.plateforme
        findViewById<TextView>(R.id.tvLieuPoste).text = candidature.lieuPoste
        updateStateDisplay()
    }

    private fun updateStateDisplay() {
        val stateWithEmoji = when (candidature.state) {
            CandidatureState.CANDIDATEE_ET_EN_ATTENTE -> "🕒 Candidature en attente"
            CandidatureState.EN_ATTENTE_APRES_ENTRETIEN -> "🕒 En attente après entretien"
            CandidatureState.EN_ATTENTE_D_UN_ENTRETIEN -> "🕒 En attente d'un entretien"
            CandidatureState.FAIRE_UN_RETOUR_POST_ENTRETIEN -> "🔄 Faire un retour post entretien"
            CandidatureState.A_RELANCEE_APRES_ENTRETIEN -> "🔄 Relancée après entretien"
            CandidatureState.A_RELANCEE -> "🔄 À relancer"
            CandidatureState.RELANCEE_ET_EN_ATTENTE -> "🕒 Relancée et en attente"
            CandidatureState.AUCUNE_REPONSE -> "🚫 Aucune réponse"
            CandidatureState.NON_RETENU -> "❌ Non retenue"
            CandidatureState.ERREUR -> "⚠️ Erreur"
            CandidatureState.NON_RETENU_APRES_ENTRETIEN -> "❌️ Non retenue après entretien"
            CandidatureState.NON_RETENU_SANS_ENTRETIEN -> "❌ Non retenue"
            CandidatureState.ACCEPTEE -> "✅ Acceptée"
        }
        findViewById<TextView>(R.id.tvEtatCandidature).text = stateWithEmoji
    }

    private fun setupRecyclerView() {
        setupContactRecyclerView()
        setupAppelRecyclerView()
        setupEntretienRecyclerView()
        setupRelanceRecyclerView()
    }

    private fun setupContactRecyclerView() {
        val contacts = contactDataRepository.loadContactsForEntreprise(candidature.entrepriseNom)
        val contactAdapter = ContactAdapter(contacts, this::onContactClicked)
        findViewById<RecyclerView>(R.id.recyclerViewContacts).apply {
            layoutManager = LinearLayoutManager(this@DetailsCandidatureActivity)
            adapter = contactAdapter
        }
    }
    private fun onContactClicked(contact: Contact) {
        startActivity(Intent(this, DetailsContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        })
    }

    private fun setupAppelRecyclerView() {
        val appels = appelDataRepository.loadAppelsForCandidature(candidature.id)
        val appelAdapter = AppelAdapter(appels, this::onAppelClicked)
        findViewById<RecyclerView>(R.id.recyclerViewAppels).apply {
            layoutManager = LinearLayoutManager(this@DetailsCandidatureActivity)
            adapter = appelAdapter
        }
    }
    private fun onAppelClicked(appel: Appel) {
        startActivity(Intent(this, DetailsAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appel.id)
        })
    }

    private fun setupEntretienRecyclerView() {
        val entretiens = entretienDataRepository.loadEntretienForCandidature(candidature.id)
        val entretienAdapter = EntretienAdapter(entretiens, this::onEntretienClicked)
        findViewById<RecyclerView>(R.id.recyclerViewEntretiens).apply {
            layoutManager = LinearLayoutManager(this@DetailsCandidatureActivity)
            adapter = entretienAdapter
        }
    }
    private fun onEntretienClicked(entretien: Entretien) {
        startActivity(Intent(this, DetailsEntretienActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
            putExtra("ENTRETIEN_ID", entretien.id)
        })
    }

    private fun setupRelanceRecyclerView() {
        val relances = relanceDataRepository.loadRelanceForCandidature(candidature.id)
        val relanceAdapter = RelanceAdapter(relances, this::onRelanceClicked)
        findViewById<RecyclerView>(R.id.recyclerViewRelances).apply {
            layoutManager = LinearLayoutManager(this@DetailsCandidatureActivity)
            adapter = relanceAdapter
        }
    }

    private fun onRelanceClicked(relance: Relance) {
        startActivity(Intent(this, DetailsRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relance.id)
        })
    }

    private fun markAsRejected() {
        candidature.state = if (candidature.entretiens.isEmpty()) CandidatureState.NON_RETENU_SANS_ENTRETIEN else CandidatureState.NON_RETENU_APRES_ENTRETIEN
        candidatureDataRepository.updateCandidature(candidature)
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("CANDIDATURE_UPDATED"))
        finish()
    }

    private fun markAsAccepted() {
        candidature.state = CandidatureState.ACCEPTEE
        candidatureDataRepository.updateCandidature(candidature)
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("CANDIDATURE_UPDATED"))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
