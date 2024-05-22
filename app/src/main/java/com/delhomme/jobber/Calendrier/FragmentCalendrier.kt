package com.delhomme.jobber.Calendrier

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FragmentCalendrier : Fragment() {
    private lateinit var hoursRecyclerView: RecyclerView
    private lateinit var currentDayTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calendrier, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hoursRecyclerView = view.findViewById(R.id.hoursRecyclerView)

        hoursRecyclerView.layoutManager = LinearLayoutManager(context)


        currentDayTextView = view.findViewById(R.id.currentDay)

        // Synchronize scrolling between RecyclerViews
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView == hoursRecyclerView) {
                    hoursRecyclerView.scrollBy(dx, dy)
                }
            }
        }

        hoursRecyclerView.addOnScrollListener(scrollListener)

        loadEventsAndHours()
        setupDayView()
        currentDayTextView.text = SimpleDateFormat("EEE, d MMM, yyyy", Locale.FRENCH).format(Date())

        view.findViewById<Button>(R.id.prevDayButton).setOnClickListener {
            adjustDay(-1)
        }
        view.findViewById<Button>(R.id.nextDayButton).setOnClickListener {
            adjustDay(1)
        }
    }

    private fun adjustDay(dayDelta: Int) {
        val dateString = currentDayTextView.text.toString()

        if (dateString.isEmpty()) {
            Log.e("FragmentCalendrier", "Current day string is empty, setting default date.")
            currentDayTextView.text = SimpleDateFormat("EEE, d MMM, yyyy", Locale.FRENCH).format(Date())
        }

        val dateFormat = SimpleDateFormat("EEE, d MMM, yyyy", Locale.FRENCH)
        try {
            val cal = Calendar.getInstance().apply {
                time = dateFormat.parse(currentDayTextView.text.toString()) ?: return
                add(Calendar.DAY_OF_YEAR, dayDelta)
            }
            currentDayTextView.text = dateFormat.format(cal.time)
            // Fetch events for the new date
            val newDate = dateFormat.parse(currentDayTextView.text.toString())
            newDate?.let {
                val eventsForDay = DataRepository(requireContext()).getEventsOn(it)
                updateRecyclerView(eventsForDay)
            }
        } catch (e: ParseException) {
            Log.e("FragmentCalendrier", "Unparseable date: $dateString", e)
        }
    }
    private fun updateRecyclerView(events: List<Event>) {
        val hoursEvents = List(24) { hour ->
            val eventsThisHour = events.filter {
                val eventCal = Calendar.getInstance()
                eventCal.time = Date(it.startTime)
                eventCal.get(Calendar.HOUR_OF_DAY) == hour
            }
            HourEvent("$hour:00", eventsThisHour)
        }
        hoursRecyclerView.adapter = CombinedHoursEventsAdapter(hoursEvents)
    }

    private fun setupDayView() {
        val dateString = currentDayTextView.text.toString()
        if (dateString.isNotEmpty()) {
            try {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("EEE, d MMMM, yyyy", Locale.FRENCH)
                val startDate = dateFormat.parse(dateString)
                calendar.time = startDate

                val events = createSampleEvents() // Make sure this returns correctly timed events
                val hoursEvents = List(24) { hour ->
                    HourEvent("$hour:00", events.filter {
                        val eventCal = Calendar.getInstance()
                        eventCal.time = Date(it.startTime)
                        eventCal.get(Calendar.HOUR_OF_DAY) == hour
                    })
                }

                hoursRecyclerView.adapter = CombinedHoursEventsAdapter(hoursEvents)
            } catch (e: ParseException) {
                Log.e("FragmentCalendrier", "Unparseable date: $dateString", e)
            }
        } else {
            Log.e("FragmentCalendrier", "Date string is empty")
        }
    }

    private fun createSampleEvents(): List<Event> {
        return listOf(
            Event(
                id = "1",
                title = "Entretien avec ABC Corp",
                description = "Discussion du role d'ingénieur logiciel",
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + 3600000,
                type = EventType.Entretien,
                relatedId = "001",
                entrepriseId = "E001"
            ),

            Event(
                id = "2",
                title = "Follow-up Call with XYZ Ltd",
                description = "Discussion on project terms",
                startTime = System.currentTimeMillis() + 7200000, // plus two hours
                endTime = System.currentTimeMillis() + 10800000, // plus three hours
                type = EventType.Appel,
                relatedId = "002",
                entrepriseId = "E002"
            )
        )
    }

    private fun loadEventsAndHours() {
        val events = DataRepository(requireContext()).getEvents()
        val hoursEvents = List(24) { hour ->
            HourEvent("$hour:00", events.filter {
                val eventCal = Calendar.getInstance()
                eventCal.time = Date(it.startTime)
                eventCal.get(Calendar.HOUR_OF_DAY) == hour
            })
        }
        hoursRecyclerView.adapter = CombinedHoursEventsAdapter(hoursEvents)
    }

}
