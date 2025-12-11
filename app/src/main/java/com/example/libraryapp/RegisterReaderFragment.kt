package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterReaderFragment : Fragment() {

    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_register_reader2, container, false)

        // Инициализируем DataManager
        dataManager = DataManager(requireContext())

        val editName = view.findViewById<EditText>(R.id.editTextReaderName)
        val editTicket = view.findViewById<EditText>(R.id.editTextReaderTicket)
        val btnSave = view.findViewById<Button>(R.id.btnSaveReader)

        btnSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val ticket = editTicket.text.toString().trim()

            if (name.isEmpty() || ticket.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем в SharedPreferences
            dataManager.saveReader(name, ticket)

            Toast.makeText(
                requireContext(),
                "Читатель '$name' сохранен!\nБилет: $ticket",
                Toast.LENGTH_LONG
            ).show()

            // Очищаем поля
            editName.text.clear()
            editTicket.text.clear()
        }

        return view
    }
}