package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class IssueBookFragment : Fragment() {

    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_issue_book2, container, false)

        dataManager = DataManager(requireContext())

        val editReaderId = view.findViewById<EditText>(R.id.editTextReaderId)
        val editBookId = view.findViewById<EditText>(R.id.editTextBookId)
        val btnIssue = view.findViewById<Button>(R.id.btnIssue)

        btnIssue.setOnClickListener {
            val readerId = editReaderId.text.toString().trim()
            val bookId = editBookId.text.toString().trim()

            if (readerId.isEmpty() || bookId.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните ID читателя и книги", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем, существует ли читатель
            val readerName = dataManager.getReader(readerId)
            if (readerName == null) {
                Toast.makeText(requireContext(), "Читатель с ID $readerId не найден!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Регистрируем выдачу - ИСПРАВЛЯЕМ ЗДЕСЬ!
            dataManager.issueBookToReader(readerId, bookId) // было issueBook

            Toast.makeText(
                requireContext(),
                "Книга $bookId выдана читателю:\n$readerName (билет: $readerId)",
                Toast.LENGTH_LONG
            ).show()

            // Очищаем поля
            editReaderId.text.clear()
            editBookId.text.clear()
        }

        return view
    }
}