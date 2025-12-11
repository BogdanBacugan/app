package com.example.libraryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ReturnBookFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_return_book2, container, false)

        val editBookId = view.findViewById<EditText>(R.id.editTextReturnBookId)
        val btnReturn = view.findViewById<Button>(R.id.btnReturnBook)

        btnReturn?.setOnClickListener {
            val bookId = editBookId?.text.toString()

            if (bookId.isNotEmpty()) {
                Toast.makeText(requireContext(),
                    "Книга $bookId возвращена в библиотеку",
                    Toast.LENGTH_SHORT).show()

                editBookId?.text?.clear()
            } else {
                Toast.makeText(requireContext(),
                    "Введите ID книги",
                    Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}