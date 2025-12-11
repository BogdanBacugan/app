package com.example.libraryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ReaderBooksFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private var readerTicket: String = ""
    private var readerName: String = ""

    // Функция для передачи данных
    fun setReaderData(ticket: String, name: String) {
        readerTicket = ticket
        readerName = name
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_reader_books, container, false)

        dataManager = DataManager(requireContext())

        val textReaderName = view.findViewById<TextView>(R.id.textReaderName)
        val textBooksList = view.findViewById<TextView>(R.id.textBooksList)
        val editBookId = view.findViewById<EditText>(R.id.editTextBookIdToIssue)
        val btnIssue = view.findViewById<Button>(R.id.btnIssueBook)
        val btnBack = view.findViewById<Button>(R.id.btnBack)

        // Устанавливаем имя читателя
        textReaderName.text = "Книги читателя: $readerName"

        // Функция обновления списка книг
        fun updateBooksList() {
            val books = dataManager.getReaderBooks(readerTicket)

            if (books.isEmpty()) {
                textBooksList.text = "У читателя пока нет книг."
            } else {
                val booksText = StringBuilder("Выданные книги:\n\n")
                for (book in books) {
                    booksText.append("• ${book.title}\n  Автор: ${book.author}\n  ID: ${book.id}\n\n")
                }
                textBooksList.text = booksText.toString()
            }
        }

        // Показываем список при открытии
        updateBooksList()

        // Кнопка выдачи книги
        btnIssue.setOnClickListener {
            val bookId = editBookId.text.toString().trim()

            if (bookId.isEmpty()) {
                Toast.makeText(requireContext(), "Введите ID книги", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем, существует ли книга
            val book = dataManager.getBook(bookId)
            if (book == null) {
                Toast.makeText(requireContext(), "Книга с ID $bookId не найдена!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Выдаем книгу читателю
            dataManager.issueBookToReader(readerTicket, bookId)

            Toast.makeText(
                requireContext(),
                "Книга '${book.title}' выдана читателю $readerName",
                Toast.LENGTH_LONG
            ).show()

            // Обновляем список и очищаем поле
            updateBooksList()
            editBookId.text.clear()
        }

        // Кнопка назад
        btnBack.setOnClickListener {
            // Возвращаемся назад
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}