package com.example.bitfit

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barChart = view.findViewById<BarChart>(R.id.barChart)

        lifecycleScope.launch {
            FoodDatabase.getInstance(requireActivity().application)
                .foodEntryDao()
                .getAllEntries()
                .collect { entries ->
                    // Summary stats
                    val total = entries.sumOf { it.calories }
                    val avg = if (entries.isEmpty()) 0 else total / entries.size
                    val count = entries.size

                    view.findViewById<TextView>(R.id.tvTotalCalories).text = total.toString()
                    view.findViewById<TextView>(R.id.tvAvgCalories).text = avg.toString()
                    view.findViewById<TextView>(R.id.tvMealCount).text = count.toString()

                    // Chart — show most recent 7 entries, oldest to newest left to right
                    val recent = entries.take(7).reversed()
                    val barEntries = recent.mapIndexed { index, entry ->
                        BarEntry(index.toFloat(), entry.calories.toFloat())
                    }
                    val labels = recent.map { it.foodName }

                    val dataSet = BarDataSet(barEntries, "Calories").apply {
                        color = 0xFF7C4DFF.toInt()       // purple_500
                        valueTextColor = Color.WHITE
                        valueTextSize = 10f
                    }

                    barChart.apply {
                        data = BarData(dataSet)
                        description.isEnabled = false
                        legend.isEnabled = false
                        setFitBars(true)
                        setBackgroundColor(Color.TRANSPARENT)

                        // X axis — food names
                        xAxis.apply {
                            valueFormatter = IndexAxisValueFormatter(labels)
                            position = XAxis.XAxisPosition.BOTTOM
                            granularity = 1f
                            textColor = Color.LTGRAY
                            textSize = 9f
                            gridColor = 0xFF2C2C2C.toInt()
                            setDrawGridLines(false)
                        }

                        // Left Y axis
                        axisLeft.apply {
                            textColor = Color.LTGRAY
                            gridColor = 0xFF2C2C2C.toInt()
                            axisMinimum = 0f
                        }

                        // Hide right Y axis
                        axisRight.isEnabled = false

                        animateY(600)
                        invalidate()
                    }
                }
        }
    }

    companion object {
        fun newInstance() = DashboardFragment()
    }
}