package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterBookFragment : Fragment() {

    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_register_book2, container, false)

        dataManager = DataManager(requireContext())

        val editTitle = view.findViewById<EditText>(R.id.editTextBookTitle)
        val editAuthor = view.findViewById<EditText>(R.id.editTextBookAuthor)
        val editBookId = view.findViewById<EditText>(R.id.editTextBookId) // теперь ID вместо года
        val btnSave = view.findViewById<Button>(R.id.btnSaveBook)

        btnSave.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val author = editAuthor.text.toString().trim()
            val bookId = editBookId.text.toString().trim()

            if (title.isEmpty() || author.isEmpty() || bookId.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем, не существует ли книга с таким ID
            if (dataManager.getBook(bookId) != null) {
                Toast.makeText(requireContext(), "Книга с ID $bookId уже существует!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем книгу
            dataManager.saveBook(title, author, bookId)

            Toast.makeText(
                requireContext(),
                "Книга '$title' сохранена!\nID: $bookId\nАвтор: $author",
                Toast.LENGTH_LONG
            ).show()

            // Очищаем поля
            editTitle.text.clear()
            editAuthor.text.clear()
            editBookId.text.clear()
        }

        return view
    }
}