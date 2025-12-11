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

        // Сохраняем список всех ID книг (только ID, не объекты Book!)
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

    // === ДАННЫЕ КНИГИ (класс) ===
    data class Book(
        val id: String,  // теперь ID строка
        val title: String,
        val author: String
    )
}