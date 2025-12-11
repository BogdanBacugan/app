package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile2, container, false)

        val textName = view.findViewById<TextView>(R.id.textEmployeeName)
        val textPosition = view.findViewById<TextView>(R.id.textEmployeePosition)
        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)

        // Заглушка данных
        textName?.text = "Иванова Мария Петровна"
        textPosition?.text = "Библиотекарь"

        btnSave?.setOnClickListener {
            Toast.makeText(requireContext(),
                "Данные сохранены (заглушка)",
                Toast.LENGTH_SHORT).show()
        }

        return view
    }
}