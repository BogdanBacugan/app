package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home2, container, false)

        // Находим кнопки
        val btnRegisterReader = view.findViewById<Button>(R.id.btnRegisterReader)
        val btnRegisterBook = view.findViewById<Button>(R.id.btnRegisterBook)
        val btnIssueBook = view.findViewById<Button>(R.id.btnIssueBook)
        val btnStats = view.findViewById<Button>(R.id.btnStats) // новая кнопка

        // В HomeFragment.kt, в onCreateView, после нахождения кнопок:
        val btnReturnBook = view.findViewById<Button>(R.id.btnReturnBook)

// Обработчик для кнопки возврата книги
        btnReturnBook?.setOnClickListener {
            Toast.makeText(requireContext(),
                "Переход к возврату книги",
                Toast.LENGTH_SHORT).show()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ReturnBookFragment())
                .addToBackStack("home")
                .commit()
        }

        // Обработчики кнопок
        btnRegisterReader?.setOnClickListener {
            Toast.makeText(requireContext(),
                "Переход на регистрацию читателя",
                Toast.LENGTH_SHORT).show()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterReaderFragment())
                .addToBackStack("home")
                .commit()
        }

        btnRegisterBook?.setOnClickListener {
            Toast.makeText(requireContext(),
                "Переход на регистрацию книги",
                Toast.LENGTH_SHORT).show()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterBookFragment())
                .addToBackStack("home")
                .commit()
        }

        btnIssueBook?.setOnClickListener {
            Toast.makeText(requireContext(),
                "Переход к выдаче книги",
                Toast.LENGTH_SHORT).show()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, IssueBookFragment()) // Новый фрагмент!
                .addToBackStack("home")
                .commit()
        }

        // НОВЫЙ ОБРАБОТЧИК ДЛЯ СТАТИСТИКИ
        btnStats?.setOnClickListener {
            Toast.makeText(requireContext(),
                "📊 Открываем статистику",
                Toast.LENGTH_SHORT).show()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StatsFragment())
                .addToBackStack("home")
                .commit()
        }

        return view
    }
}