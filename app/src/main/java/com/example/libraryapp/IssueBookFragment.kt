package com.example.libraryapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class IssueBookFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var spinnerReaders: Spinner
    private lateinit var spinnerBooks: Spinner
    private lateinit var layoutAvailableBooks: LinearLayout
    private lateinit var textSelectedReader: TextView
    private lateinit var textSelectedBook: TextView
    private lateinit var btnIssue: Button
    private lateinit var btnBack: Button

    private var selectedReaderTicket: String = ""
    private var selectedBookId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_issue_book2, container, false)

        dataManager = DataManager(requireContext())

        // Инициализация элементов
        spinnerReaders = view.findViewById(R.id.spinnerReaders)
        spinnerBooks = view.findViewById(R.id.spinnerBooks)
        layoutAvailableBooks = view.findViewById(R.id.layoutAvailableBooks)
        textSelectedReader = view.findViewById(R.id.textSelectedReader)
        textSelectedBook = view.findViewById(R.id.textSelectedBook)
        btnIssue = view.findViewById(R.id.btnIssue)
        btnBack = view.findViewById(R.id.btnBack)

        // Загружаем данные
        loadReaders()
        loadAvailableBooks()

        // Обработчик выбора читателя
        spinnerReaders.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val readerItem = parent?.getItemAtPosition(position) as String
                    selectedReaderTicket = readerItem.substringAfter("Билет: ").trim()
                    val readerName = readerItem.substringBefore("\n")

                    textSelectedReader.text = "Читатель: $readerName"
                    updateIssueButton()
                } else {
                    selectedReaderTicket = ""
                    textSelectedReader.text = "Выберите читателя"
                    updateIssueButton()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedReaderTicket = ""
                textSelectedReader.text = "Выберите читателя"
                updateIssueButton()
            }
        }

        // Обработчик выбора книги
        spinnerBooks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val bookItem = parent?.getItemAtPosition(position) as String
                    selectedBookId = bookItem.substringAfter("ID: ").trim()
                    val bookTitle = bookItem.substringBefore("\n")

                    textSelectedBook.text = "Книга: $bookTitle"
                    updateIssueButton()
                } else {
                    selectedBookId = ""
                    textSelectedBook.text = "Выберите книгу"
                    updateIssueButton()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedBookId = ""
                textSelectedBook.text = "Выберите книгу"
                updateIssueButton()
            }
        }

        // Кнопка выдачи
        btnIssue.setOnClickListener {
            if (selectedReaderTicket.isNotEmpty() && selectedBookId.isNotEmpty()) {
                showIssueConfirmationDialog()
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
        readerList.add("-- Выберите читателя --")

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

    private fun loadAvailableBooks() {
        val allBooks = dataManager.getAllBooks()

        // Фильтруем только свободные книги
        val availableBooks = allBooks.filter { book ->
            !isBookIssued(book.id)
        }

        // Создаем список для Spinner
        val bookList = mutableListOf<String>()
        bookList.add("-- Выберите книгу --")

        for (book in availableBooks) {
            bookList.add("${book.title}\nID: ${book.id}")
        }

        // Настраиваем Spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            bookList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBooks.adapter = adapter

        // Показываем список доступных книг
        showAvailableBooks(availableBooks)
    }

    private fun showAvailableBooks(books: List<DataManager.Book>) {
        layoutAvailableBooks.removeAllViews()

        if (books.isEmpty()) {
            // Сообщение, когда нет доступных книг
            val emptyCard = layoutInflater.inflate(R.layout.item_empty_books, null)
            layoutAvailableBooks.addView(emptyCard)
        } else {
            // Заголовок внутри карточки
            val header = TextView(requireContext())
            header.text = "📚 Доступно для выдачи: ${books.size} книг"
            header.textSize = 16f
                    header.setTextColor(resources.getColor(android.R.color.black))
            header.setPadding(0, 0, 0, 16)
            layoutAvailableBooks.addView(header)

            for (book in books) {
                // Создаем красивую карточку для каждой книги
                val bookCard = layoutInflater.inflate(R.layout.item_available_book, null)

                val textTitle = bookCard.findViewById<TextView>(R.id.textBookTitle)
                val textAuthor = bookCard.findViewById<TextView>(R.id.textBookAuthor)
                val textId = bookCard.findViewById<TextView>(R.id.textBookId)
                val btnSelect = bookCard.findViewById<TextView>(R.id.btnSelectBook)

                textTitle.text = book.title
                textAuthor.text = "✍️ ${book.author}"
                textId.text = "🆔 ID: ${book.id}"

                // Кнопка для быстрого выбора этой книги
                btnSelect.setOnClickListener {
                    // Находим позицию этой книги в спиннере
                    val bookList = mutableListOf<String>()
                    bookList.add("-- Выберите книгу --")
                    val allAvailableBooks = dataManager.getAllBooks().filter { !isBookIssued(it.id) }
                    for (availableBook in allAvailableBooks) {
                        bookList.add("${availableBook.title}\nID: ${availableBook.id}")
                    }

                    val position = bookList.indexOfFirst { it.contains(book.id) }
                    if (position > 0) {
                        spinnerBooks.setSelection(position)
                    }
                }

                layoutAvailableBooks.addView(bookCard)

                // Добавляем отступ между карточками
                val space = View(requireContext())
                space.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    8
                )
                layoutAvailableBooks.addView(space)
            }
        }
    }

    private fun isBookIssued(bookId: String): Boolean {
        val readers = dataManager.getAllReaders()
        for ((ticket, _) in readers) {
            val readerBooks = dataManager.getReaderBooks(ticket)
            if (readerBooks.any { it.id == bookId }) {
                return true
            }
        }
        return false
    }

    private fun updateIssueButton() {
        val isReady = selectedReaderTicket.isNotEmpty() && selectedBookId.isNotEmpty()
        btnIssue.isEnabled = isReady
        btnIssue.alpha = if (isReady) 1.0f else 0.5f
    }

    private fun showIssueConfirmationDialog() {
        val readerName = dataManager.getReader(selectedReaderTicket) ?: "Читатель"
        val book = dataManager.getBook(selectedBookId)

        if (book == null) {
            Toast.makeText(requireContext(), "Книга не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Выдача книги")
            .setMessage("Подтвердите выдачу книги:\n\n" +
                    "📚 Книга: ${book.title}\n" +
                    "✍️ Автор: ${book.author}\n" +
                    "👤 Читатель: $readerName\n" +
                    "🆔 ID книги: ${book.id}")
            .setPositiveButton("✅ Выдать книгу") { _, _ ->
                issueBookToReader()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun issueBookToReader() {
        val readerName = dataManager.getReader(selectedReaderTicket) ?: "Читатель"
        val book = dataManager.getBook(selectedBookId)

        if (book != null) {
            dataManager.issueBookToReader(selectedReaderTicket, selectedBookId)

            Toast.makeText(
                requireContext(),
                "✅ Книга '${book.title}' выдана читателю $readerName!",
                Toast.LENGTH_LONG
            ).show()

            // Обновляем список книг
            loadAvailableBooks()

            // Сбрасываем выбор
            spinnerReaders.setSelection(0)
            spinnerBooks.setSelection(0)
            selectedReaderTicket = ""
            selectedBookId = ""
            textSelectedReader.text = "Выберите читателя"
            textSelectedBook.text = "Выберите книгу"
            updateIssueButton()
        }
    }
}