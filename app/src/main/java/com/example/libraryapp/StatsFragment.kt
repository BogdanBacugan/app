package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class StatsFragment : Fragment() {

    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_stats2, container, false)

        dataManager = DataManager(requireContext())

        val btnCalculate = view.findViewById<Button>(R.id.btnCalculateStats)
        val textStats = view.findViewById<TextView>(R.id.textStats)

        btnCalculate.setOnClickListener {
            val readers = dataManager.getAllReaders()
            val books = dataManager.getAllBooks()
            //val issues = dataManager.getActiveIssues()

            val statsText = """
                📊 СТАТИСТИКА БИБЛИОТЕКИ 📊
                
                📚 Всего книг: ${books.size}
                👥 Всего читателей: ${readers.size}
                
                
                📈 Последние добавления:
                ${if (books.isNotEmpty()) "Книга: ${books.last().title}" else "Книг нет"}
                ${if (readers.isNotEmpty()) "Читатель: ${readers.values.last()}" else "Читателей нет"}
                
                💾 Данные сохраняются между запусками приложения!
            """.trimIndent()

            textStats.text = statsText
            Toast.makeText(requireContext(), "Статистика рассчитана!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}