package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class ReadersListFragment : Fragment() {

    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_readers_list2, container, false)

        dataManager = DataManager(requireContext())

        val btnRefresh = view.findViewById<Button>(R.id.btnRefreshReaders)
        val layoutContainer = view.findViewById<LinearLayout>(R.id.layoutContainer)

        // Если нет LinearLayout, создаем его
        if (layoutContainer == null) {
            // Изменяем XML или используем другой подход
            return simpleReadersList(view)
        }

        // Функция для обновления списка
        fun updateReadersList() {
            layoutContainer.removeAllViews() // очищаем старый список

            val readers = dataManager.getAllReaders()

            if (readers.isEmpty()) {
                val textInfo = TextView(requireContext())
                textInfo.text = "Читателей пока нет.\nЗарегистрируйте первого читателя!"
                textInfo.textSize = 16f
                textInfo.setPadding(0, 20, 0, 20)
                layoutContainer.addView(textInfo)
            } else {
                for ((ticket, name) in readers) {
                    // Создаем TextView для каждого читателя
                    val readerView = TextView(requireContext())
                    readerView.text = "👤 $name\n📋 Билет: $ticket"
                    readerView.textSize = 18f
                    readerView.setPadding(0, 20, 0, 20)

                    // Делаем кликабельным
                    readerView.setOnClickListener {
                        // При нажатии открываем книги читателя
                        val readerBooksFragment = ReaderBooksFragment().apply {
                            setReaderData(ticket, name)
                        }

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, readerBooksFragment)
                            .addToBackStack("readers_list")
                            .commit()
                    }

                    layoutContainer.addView(readerView)

                    // Добавляем разделитель
                    val divider = View(requireContext())
                    divider.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )
                    divider.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                    layoutContainer.addView(divider)
                }
            }
        }

        // Показываем список при открытии
        updateReadersList()

        btnRefresh.setOnClickListener {
            updateReadersList()
            Toast.makeText(requireContext(), "Список обновлен!", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    // Упрощенный вариант если нет LinearLayout
    private fun simpleReadersList(view: View): View {
        val btnRefresh = view.findViewById<Button>(R.id.btnRefreshReaders)
        val textInfo = view.findViewById<TextView>(R.id.textReadersInfo)

        // Функция для обновления списка
        fun updateReadersList() {
            val readers = dataManager.getAllReaders()

            if (readers.isEmpty()) {
                textInfo.text = "Читателей пока нет.\nЗарегистрируйте первого читателя!"
            } else {
                val readerListText = StringBuilder("Список читателей (нажмите для просмотра книг):\n\n")
                for ((ticket, name) in readers) {
                    readerListText.append("👤 $name\n📋 Билет: $ticket\n\n")
                }
                textInfo.text = readerListText.toString()

                // Делаем TextView кликабельным
                textInfo.setOnClickListener {
                    // Можно добавить диалог или другую логику
                    Toast.makeText(requireContext(), "Нажмите на конкретного читателя в будущей версии", Toast.LENGTH_SHORT).show()
                }
            }
        }

        updateReadersList()

        btnRefresh.setOnClickListener {
            updateReadersList()
            Toast.makeText(requireContext(), "Список обновлен!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}