package com.example.libraryapp

import android.content.Context
import android.content.SharedPreferences

class DataManager(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("library_app_data", Context.MODE_PRIVATE)

    // === ЧИТАТЕЛИ ===
    fun saveReader(name: String, ticketNumber: String) {
        val editor = sharedPref.edit()
        editor.putString("reader_$ticketNumber", name)

        val readersList = getReadersTickets().toMutableSet()
        readersList.add(ticketNumber)
        editor.putStringSet("all_readers_tickets", readersList)

        editor.apply()
    }

    fun getReader(ticketNumber: String): String? {
        return sharedPref.getString("reader_$ticketNumber", null)
    }

    fun getAllReaders(): Map<String, String> {
        val readers = mutableMapOf<String, String>()
        val tickets = getReadersTickets()

        for (ticket in tickets) {
            val name = getReader(ticket)
            if (name != null) {
                readers[ticket] = name
            }
        }
        return readers
    }

    private fun getReadersTickets(): Set<String> {
        return sharedPref.getStringSet("all_readers_tickets", emptySet()) ?: emptySet()
    }

    // === КНИГИ ===
    fun saveBook(title: String, author: String, bookId: String) {
        val editor = sharedPref.edit()

        // Сохраняем книгу по её ID
        editor.putString("book_${bookId}_title", title)
        editor.putString("book_${bookId}_author", author)

        // Сохраняем список всех ID книг
        val booksList = getBooksIds().toMutableSet()
        booksList.add(bookId)
        editor.putStringSet("all_books_ids", booksList)

        editor.apply()
    }

    fun getBook(bookId: String): Book? {
        val title = sharedPref.getString("book_${bookId}_title", "") ?: ""
        val author = sharedPref.getString("book_${bookId}_author", "") ?: ""

        if (title.isEmpty()) return null

        return Book(bookId, title, author)
    }

    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val bookIds = getBooksIds()

        for (bookId in bookIds) {
            val book = getBook(bookId)
            if (book != null) {
                books.add(book)
            }
        }
        return books
    }

    private fun getBooksIds(): Set<String> {
        return sharedPref.getStringSet("all_books_ids", emptySet()) ?: emptySet()
    }

    // === ВЫДАЧА КНИГ ЧИТАТЕЛЮ ===
    fun issueBookToReader(readerTicket: String, bookId: String) {
        val editor = sharedPref.edit()

        // Получаем текущие книги читателя (только ID книг!)
        val readerBooks = getReaderBookIds(readerTicket).toMutableSet()
        readerBooks.add(bookId)

        // Сохраняем обновленный список (только ID!)
        editor.putStringSet("reader_${readerTicket}_books", readerBooks)

        // Сохраняем историю выдачи
        saveIssueHistory(readerTicket, bookId)

        editor.apply()
    }

    // Получаем ID книг читателя
    private fun getReaderBookIds(readerTicket: String): Set<String> {
        return sharedPref.getStringSet("reader_${readerTicket}_books", emptySet()) ?: emptySet()
    }

    // Получаем книги читателя (объекты Book)
    fun getReaderBooks(readerTicket: String): List<Book> {
        val books = mutableListOf<Book>()
        val bookIds = getReaderBookIds(readerTicket)

        for (bookId in bookIds) {
            val book = getBook(bookId)
            if (book != null) {
                books.add(book)
            }
        }
        return books
    }

    // === ВОЗВРАТ КНИГИ ОТ ЧИТАТЕЛЯ ===
    fun returnBookFromReader(readerTicket: String, bookId: String) {
        val editor = sharedPref.edit()

        // Получаем текущие книги читателя
        val readerBooks = getReaderBookIds(readerTicket).toMutableSet()

        // Удаляем книгу из списка
        if (readerBooks.remove(bookId)) {
            // Сохраняем обновленный список
            editor.putStringSet("reader_${readerTicket}_books", readerBooks)
            editor.apply()

            // Сохраняем историю возвратов
            saveReturnHistory(readerTicket, bookId)
        }
    }

    // === ИСТОРИЯ ВЫДАЧИ ===
    private fun saveIssueHistory(readerTicket: String, bookId: String) {
        val editor = sharedPref.edit()
        val issueId = System.currentTimeMillis().toString()

        editor.putString("issue_${issueId}_reader", readerTicket)
        editor.putString("issue_${issueId}_book", bookId)
        editor.putString("issue_${issueId}_date", issueId)
        editor.putBoolean("issue_${issueId}_returned", false)

        editor.apply()
    }

    // === ИСТОРИЯ ВОЗВРАТОВ ===
    private fun saveReturnHistory(readerTicket: String, bookId: String) {
        val editor = sharedPref.edit()
        val returnId = System.currentTimeMillis().toString()

        editor.putString("return_${returnId}_reader", readerTicket)
        editor.putString("return_${returnId}_book", bookId)
        editor.putString("return_${returnId}_date", returnId)

        editor.apply()
    }

    // === СТАТИСТИКА ===
    fun getStats(): Stats {
        val readersCount = getAllReaders().size
        val booksCount = getAllBooks().size

        // Считаем выданные книги
        var issuedBooksCount = 0
        for ((ticket, _) in getAllReaders()) {
            issuedBooksCount += getReaderBooks(ticket).size
        }

        return Stats(
            totalReaders = readersCount,
            totalBooks = booksCount,
            issuedBooks = issuedBooksCount
        )
    }

    // === ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ===

    // Класс книги
    data class Book(
        val id: String,  // ID книги
        val title: String,
        val author: String
    )

    // Класс статистики
    data class Stats(
        val totalReaders: Int,
        val totalBooks: Int,
        val issuedBooks: Int
    )

    // === ДОПОЛНИТЕЛЬНЫЕ ФУНКЦИИ (для теста) ===

    // Очистить все данные (для тестирования)
    fun clearAllData() {
        sharedPref.edit().clear().apply()
    }

    // Добавить тестовые данные
    fun addTestData() {
        // Добавляем тестовых читателей
        saveReader("Иванов Иван Иванович", "1001")
        saveReader("Петрова Анна Сергеевна", "1002")
        saveReader("Сидоров Алексей Петрович", "1003")

        // Добавляем тестовые книги
        saveBook("Мастер и Маргарита", "Михаил Булгаков", "2001")
        saveBook("Преступление и наказание", "Федор Достоевский", "2002")
        saveBook("1984", "Джордж Оруэлл", "2003")
        saveBook("Война и мир", "Лев Толстой", "2004")
        saveBook("Гарри Поттер и философский камень", "Джоан Роулинг", "2005")

        // Выдаем несколько книг
        issueBookToReader("1001", "2001")
        issueBookToReader("1001", "2002")
        issueBookToReader("1002", "2003")
    }
}