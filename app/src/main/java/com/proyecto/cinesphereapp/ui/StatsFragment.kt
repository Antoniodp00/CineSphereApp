package com.proyecto.cinesphereapp.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.MiListaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream


class StatsFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var tvResumen: TextView
    private lateinit var btnExportar: Button
    private lateinit var dao: MiListaDao
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = MiListaDao(requireContext())
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        userId = prefs.getInt("USER_ID", -1)

        pieChart = view.findViewById(R.id.pieChart)
        tvResumen = view.findViewById(R.id.tvResumenTotal)
        btnExportar = view.findViewById(R.id.btnExportarGrafica)

        configurarGrafico()

        if (userId != -1) {
            cargarDatosGrafico()
        }

        btnExportar.setOnClickListener {
            guardarGraficaEnGaleria()
        }
    }

    private fun configuringColors(): List<Int> {
        // Colores personalizados para cada estado
        return listOf(
            Color.parseColor("#4CAF50"), // VISTO (Verde)
            Color.parseColor("#FFC107"), // PENDIENTE (Amarillo)
            Color.parseColor("#2196F3"), // VIENDO (Azul)
            Color.parseColor("#F44336")  // ABANDONADA (Rojo)
        )
    }

    private fun configurarGrafico() {
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false // Quitar descripción pequeña
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.textColor = Color.WHITE
        pieChart.animateY(1400) // Animación al entrar

        // Texto central
        pieChart.centerText = "Mis Películas"
        pieChart.setCenterTextSize(18f)
        pieChart.setCenterTextColor(Color.WHITE)
    }

    private fun cargarDatosGrafico() {
        lifecycleScope.launch(Dispatchers.IO) {
            // 1. Obtener datos de la BD SQLite
            val total = dao.contarPeliculas(userId)
            val countVisto = dao.contarPorEstado(userId, "VISTO")
            val countPendiente = dao.contarPorEstado(userId, "PENDIENTE")
            val countViendo = dao.contarPorEstado(userId, "VIENDO")
            val countAbandonada = dao.contarPorEstado(userId, "ABANDONADA")

            // 2. Crear entradas para el gráfico
            val entries = ArrayList<PieEntry>()
            if (countVisto > 0) entries.add(PieEntry(countVisto.toFloat(), "Vistas"))
            if (countPendiente > 0) entries.add(PieEntry(countPendiente.toFloat(), "Pendientes"))
            if (countViendo > 0) entries.add(PieEntry(countViendo.toFloat(), "Viendo"))
            if (countAbandonada > 0) entries.add(PieEntry(countAbandonada.toFloat(), "Abandonadas"))

            withContext(Dispatchers.Main) {
                tvResumen.text = "Total de películas guardadas: $total"

                if (entries.isNotEmpty()) {
                    val dataSet = PieDataSet(entries, "")
                    dataSet.colors = configuringColors()
                    dataSet.valueTextColor = Color.WHITE
                    dataSet.valueTextSize = 14f
                    dataSet.valueFormatter = PercentFormatter(pieChart)

                    val data = PieData(dataSet)
                    pieChart.data = data
                    pieChart.invalidate() // Refrescar gráfica
                } else {
                    pieChart.centerText = "Sin datos"
                }
            }
        }
    }

    private fun guardarGraficaEnGaleria() {
        // Obtenemos el Bitmap del gráfico
        val bitmap = pieChart.chartBitmap
        val filename = "CineSphere_Stats_${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null
        var imageUri: android.net.Uri? = null

        try {
            val contentResolver = requireContext().contentResolver

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CineSphere")
                }
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { contentResolver.openOutputStream(it) }
            } else {
                // Para Android 9 e inferiores (Método clásico)
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = java.io.File(imagesDir, filename)
                fos = java.io.FileOutputStream(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(requireContext(), "Gráfica guardada en la Galería", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al guardar imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (userId != -1) cargarDatosGrafico()
    }
}