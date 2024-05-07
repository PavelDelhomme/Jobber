package com.delhomme.jobber


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.delhomme.jobber.AppelPacket.AppelAddActivity
import com.delhomme.jobber.CandidaturePacket.CandidatureAddActivity
import com.delhomme.jobber.ContactPacket.ContactAddActivity
import com.delhomme.jobber.EntreprisePacket.EntrepriseAddActivity
import com.delhomme.jobber.EntretienPacket.EntretienAddActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // Configuration du ViewPager avec un adapter personnalisé
        val viewPagerAdapter = MainViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        // Relier le TabLayout au ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Dashboard"
                1 -> "Candidatures"
                2 -> "Appels"
                3 -> "Contacts"
                4 -> "Entretiens"
                5 -> "Entreprises"
                else -> "Autres"
            }
        }.attach()

        // Configuration du Floating Action Button
        val fabMenuJobber = findViewById<FloatingActionButton>(R.id.fabMenuJobber)
        fabMenuJobber.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_add_items, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_add_candidature -> {
                    startActivity(Intent(this, CandidatureAddActivity::class.java))
                    true
                }
                R.id.menu_add_contact -> {
                    startActivity(Intent(this, ContactAddActivity::class.java))
                    true
                }
                R.id.menu_add_entretien -> {
                    startActivity(Intent(this, EntretienAddActivity::class.java))
                    true
                }
                R.id.menu_add_appel -> {
                    startActivity(Intent(this, AppelAddActivity::class.java))
                    true
                }
                R.id.menu_add_entreprise -> {
                    startActivity(Intent(this, EntrepriseAddActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}