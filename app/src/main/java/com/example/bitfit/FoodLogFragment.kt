package com.example.bitfit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class FoodLogFragment : Fragment() {

    private lateinit var adapter: FoodEntryAdapter
    private val entries = mutableListOf<FoodEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_food_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvFoodEntries)
        adapter = FoodEntryAdapter(entries)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<Button>(R.id.btnAddNewFood).setOnClickListener {
            //startActivity(Intent(requireContext(), AddEntryActivity::class.java))
            (requireActivity() as MainActivity).launchAddEntry()
        }

        lifecycleScope.launch {
            FoodDatabase.getInstance(requireActivity().application)
                .foodEntryDao()
                .getAllEntries()
                .collect { dbEntries ->
                    adapter.updateData(dbEntries)
                }
        }
    }

    companion object {
        fun newInstance() = FoodLogFragment()
    }
}