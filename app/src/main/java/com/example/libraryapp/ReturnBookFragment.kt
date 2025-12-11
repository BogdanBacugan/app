package com.example.libraryapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class ReturnBookFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var spinnerReaders: Spinner
    private lateinit var layoutBooksContainer: LinearLayout
    private lateinit var textSelectedReader: TextView
    private var selectedReaderTicket: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_return_book2, container, false)

        dataManager = DataManager(requireContext())

        spinnerReaders = view.findViewById(R.id.spinnerReaders)
        layoutBooksContainer = view.findViewById(R.id.layoutBooksContainer)
        textSelectedReader = view.findViewById(R.id.textSelectedReader)
        val btnBack = view.findViewById<Button>(R.id.btnBack)

        // Загружаем список читателей
        loadReaders()

        // Обработчик выбора читателя
        spinnerReaders.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Позиция 0 - это подсказка
                    val readerItem = parent?.getItemAtPosition(position) as String
                    // Извлекаем номер билета из строки
                    selectedReaderTicket = readerItem.substringAfter("Билет: ").trim()
                    val readerName = readerItem.substringBefore("\n")

                    textSelectedReader.text = "Выбран: $readerName"
                    showReaderBooks(selectedReaderTicket)
                } else {
                    selectedReaderTicket = ""
                    textSelectedReader.text = "Выберите читателя"
                    layoutBooksContainer.removeAllViews()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedReaderTicket = ""
                textSelectedReader.text = "Выберите читателя"
            }
        }

        // Кнопка назад
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadReaders() {
        val readers = dataManager.getAllReaders()

        // Создаем список для Spinner
        val readerList = mutableListOf<String>()
        readerList.add("-- Выберите читателя --") // Первый элемент - подсказка

        for ((ticket, name) in readers) {
            readerList.add("$name\nБилет: $ticket")
        }

        // Настраиваем Spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            readerList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerReaders.adapter = adapter
    }

    private fun showReaderBooks(readerTicket: String) {
        layoutBooksContainer.removeAllViews()

        val books = dataManager.getReaderBooks(readerTicket)

        if (books.isEmpty()) {
            val emptyText = TextView(requireContext())
            emptyText.text = "У этого читателя нет книг на руках"
            emptyText.textSize = 16f
            emptyText.setPadding(0, 40, 0, 40)
            emptyText.gravity = View.TEXT_ALIGNMENT_CENTER
            layoutBooksContainer.addView(emptyText)
        } else {
            val readerName = dataManager.getReader(readerTicket) ?: "Читатель"

            // Заголовок
            val header = TextView(requireContext())
            header.text = "Книги у $readerName (${books.size} шт.):"
            header.textSize = 18f
            header.setPadding(0, 0, 0, 16)
            layoutBooksContainer.addView(header)

            for (book in books) {
                // Создаем карточку для книги
                val bookCard = layoutInflater.inflate(R.layout.item_book_return, null)

                val textTitle = bookCard.findViewById<TextView>(R.id.textBookTitle)
                val textAuthor = bookCard.findViewById<TextView>(R.id.textBookAuthor)
                val textId = bookCard.findViewById<TextView>(R.id.textBookId)
                val btnReturn = bookCard.findViewById<Button>(R.id.btnReturnBook)

                textTitle.text = book.title
                textAuthor.text = "Автор: ${book.author}"
                textId.text = "ID: ${book.id}"

                // Кнопка возврата
                btnReturn.setOnClickListener {
                    showReturnConfirmationDialog(readerTicket, book.id, book.title, readerName)
                }

                layoutBooksContainer.addView(bookCard)

                // Добавляем отступ между карточками
                val space = View(requireContext())
                space.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    12
                )
                layoutBooksContainer.addView(space)
            }
        }
    }

    private fun showReturnConfirmationDialog(
        readerTicket: String,
        bookId: String,
        bookTitle: String,
        readerName: String
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Возврат книги")
            .setMessage("Подтвердите возврат книги:\n\n" +
                    "📚 Книга: $bookTitle\n" +
                    "👤 Читатель: $readerName\n" +
                    "🆔 ID книги: $bookId")
            .setPositiveButton("Вернуть книгу") { _, _ ->
                returnBook(readerTicket, bookId, bookTitle, readerName)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun returnBook(readerTicket: String, bookId: String, bookTitle: String, readerName: String) {
        // Реальная логика возврата
        dataManager.returnBookFromReader(readerTicket, bookId)

        Toast.makeText(
            requireContext(),
            "✅ Книга '$bookTitle' успешно возвращена!\nЧитатель: $readerName",
            Toast.LENGTH_LONG
        ).show()

        // Обновляем список книг
        showReaderBooks(readerTicket)
    }
}