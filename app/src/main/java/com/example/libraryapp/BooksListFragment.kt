package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class BooksListFragment : Fragment() {

    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_books_list2, container, false)

        dataManager = DataManager(requireContext())

        val btnRefresh = view.findViewById<Button>(R.id.btnRefreshBooks)
        val textInfo = view.findViewById<TextView>(R.id.textBooksInfo)

        // Функция для обновления списка
        fun updateBooksList() {
            val books = dataManager.getAllBooks()

            if (books.isEmpty()) {
                textInfo.text = "Книг пока нет.\nЗарегистрируйте первую книгу!"
            } else {
                val booksListText = StringBuilder("Список книг:\n\n")
                for (book in books) {
                    booksListText.append("📖 ${book.title}\n")
                    booksListText.append("   Автор: ${book.author}\n")
                    booksListText.append("   ID: ${book.id}\n\n")  // Теперь показываем ID вместо года
                }
                textInfo.text = booksListText.toString()
            }
        }

        // Показываем список при открытии
        updateBooksList()

        btnRefresh.setOnClickListener {
            updateBooksList()
            Toast.makeText(requireContext(), "Список обновлен!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}