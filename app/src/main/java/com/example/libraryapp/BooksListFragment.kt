package com.example.libraryapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class BooksListFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var layoutContainer: LinearLayout
    private lateinit var textSearchInfo: TextView
    private lateinit var editTextSearch: EditText

    // Список всех книг для фильтрации
    private var allBooks: List<DataManager.Book> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_books_list2, container, false)

        dataManager = DataManager(requireContext())

        layoutContainer = view.findViewById(R.id.layoutBooksContainer)
        textSearchInfo = view.findViewById(R.id.textSearchInfo)
        editTextSearch = view.findViewById(R.id.editTextSearch)

        // Загружаем все книги
        loadAllBooks()

        // Настраиваем поиск
        setupSearch()

        // Показываем все книги при открытии
        showBooks(allBooks)

        return view
    }

    private fun loadAllBooks() {
        allBooks = dataManager.getAllBooks()
    }

    private fun setupSearch() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBooks(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Кнопка поиска на клавиатуре
        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                filterBooks(editTextSearch.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun filterBooks(query: String) {
        if (query.isEmpty()) {
            // Если запрос пустой, показываем все книги
            showBooks(allBooks)
        } else {
            // Фильтруем книги по названию или автору
            val filteredBooks = allBooks.filter { book ->
                book.title.contains(query, ignoreCase = true) ||
                        book.author.contains(query, ignoreCase = true) ||
                        book.id.contains(query, ignoreCase = true)
            }
            showBooks(filteredBooks)
        }
    }

    private fun showBooks(books: List<DataManager.Book>) {
        layoutContainer.removeAllViews()

        if (books.isEmpty()) {
            val emptyText = TextView(requireContext())
            emptyText.text = if (editTextSearch.text.isNotEmpty()) {
                "Книги по запросу '${editTextSearch.text}' не найдены"
            } else {
                "📚 Книг пока нет.\nЗарегистрируйте первую книгу!"
            }
            emptyText.textSize = 16f
            emptyText.setTextColor(resources.getColor(android.R.color.darker_gray))
            emptyText.setPadding(0, 40, 0, 40)
            emptyText.gravity = View.TEXT_ALIGNMENT_CENTER
            layoutContainer.addView(emptyText)
        } else {
            for (book in books) {
                // Создаем карточку для книги
                val bookCard = layoutInflater.inflate(R.layout.item_book_card, null)

                val textTitle = bookCard.findViewById<TextView>(R.id.textBookTitle)
                val textAuthor = bookCard.findViewById<TextView>(R.id.textBookAuthor)
                val textId = bookCard.findViewById<TextView>(R.id.textBookId)
                val textStatus = bookCard.findViewById<TextView>(R.id.textBookStatus)

                textTitle.text = book.title
                textAuthor.text = "Автор: ${book.author}"
                textId.text = "ID: ${book.id}"

                // Проверяем статус книги (выдана или свободна)
                val isBookIssued = isBookIssued(book.id)
                if (isBookIssued) {
                    textStatus.text = "🔴 Выдана"
                    textStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                    textStatus.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                    textStatus.visibility = View.VISIBLE
                } else {
                    textStatus.text = "🟢 Свободна"
                    textStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                    textStatus.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                    textStatus.visibility = View.VISIBLE
                }

                // Клик по карточке для подробной информации
                bookCard.setOnClickListener {
                    // Можно добавить диалог с деталями книги
                    showBookDetails(book)
                }

                layoutContainer.addView(bookCard)

                // Добавляем отступ между карточками
                val space = View(requireContext())
                space.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    16
                )
                layoutContainer.addView(space)
            }
        }

        // Обновляем информацию о поиске
        textSearchInfo.text = if (editTextSearch.text.isNotEmpty()) {
            "Найдено книг: ${books.size} (запрос: '${editTextSearch.text}')"
        } else {
            "Всего книг: ${books.size}"
        }
    }

    // Проверяем, выдана ли книга (нужно добавить в DataManager)
    private fun isBookIssued(bookId: String): Boolean {
        // Ищем книгу у всех читателей
        val readers = dataManager.getAllReaders()
        for ((ticket, _) in readers) {
            val readerBooks = dataManager.getReaderBooks(ticket)
            if (readerBooks.any { it.id == bookId }) {
                return true
            }
        }
        return false
    }

    private fun showBookDetails(book: DataManager.Book) {
        // Простой Toast с информацией о книге
        val status = if (isBookIssued(book.id)) "🔴 Выдана" else "🟢 Свободна"
        val message = """
            📚 ${book.title}
            ✍️ ${book.author}
            🆔 ID: ${book.id}
            📊 Статус: $status
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Информация о книге")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}