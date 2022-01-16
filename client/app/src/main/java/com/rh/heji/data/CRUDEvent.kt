package com.rh.heji.data

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.room.Entity
import com.rh.heji.AppViewModel
import com.rh.heji.Event
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Category
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.BookRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.CategoryEntity
import retrofit2.http.DELETE
import java.util.*

/**
 *Date: 2021/6/17
 *Author: 锅得铁
 *# entity 为 @Entity类型
 */
class EventMessage(var crud: CRUD, var entity: Any)

enum class CRUD {
    CREATE, READ, UPDATE, DELETE
}

object DataBus {
    fun post(eventMessage: EventMessage) {
        AppViewModel.get().localDataEvent.postValue(eventMessage)
    }
    fun post( crud: CRUD,  entity: Any) {
        AppViewModel.get().localDataEvent.postValue(EventMessage(crud,entity))
    }
    fun subscriberForever(observer: (EventMessage) -> Unit) {
        AppViewModel.get().localDataEvent.observeForever(observer)
    }

    fun subscriber(lifecycleOwner: LifecycleOwner, observer: (EventMessage) -> Unit) {
        AppViewModel.get().localDataEvent.observe(lifecycleOwner, observer)
    }

    private fun test() {
        subscriberForever {
            if (it.entity is Book) {
                when (it.crud) {
                    CRUD.DELETE -> "delete"
                    CRUD.READ -> "read"
                    CRUD.UPDATE -> "update"
                    CRUD.CREATE -> "update"
                }
            }
        }
    }
}

class BookTask(private val crud: CRUD, val book: Book) {
    private val bookRepository=BookRepository()
    suspend fun sync() {
        when (crud) {
            CRUD.DELETE -> {
                bookRepository.deleteBook(book_id = book.id)
            }
            CRUD.READ -> {
                "do nothing"
            }
            CRUD.UPDATE -> {
                bookRepository.updateBook(book)
            }
            CRUD.CREATE -> {
                bookRepository.addBook(book)
            }
        }
    }
}
class BillTask(private val crud: CRUD, val bill: Bill) {
    private val billRepository=BillRepository()
    suspend fun sync() {
        when (crud) {
            CRUD.DELETE -> {
                billRepository.deleteBill(_id = bill.id)
            }
            CRUD.READ -> {
                "do nothing"
            }
            CRUD.UPDATE -> {
                billRepository.updateBill(bill)
            }
            CRUD.CREATE -> {
                billRepository.addBill(bill)
            }
        }
    }
}
class CategoryTask(private val crud: CRUD, val category: Category) {
    private val categoryRepository=CategoryRepository()
    suspend fun sync() {
        when (crud) {
            CRUD.DELETE -> {
                categoryRepository.deleteCategory(_id = category.id)
            }
            CRUD.READ -> {
                "do nothing"
            }
            CRUD.UPDATE -> {
                categoryRepository.updateCategory(category)
            }
            CRUD.CREATE -> {
                categoryRepository.addCategory(CategoryEntity(category),category.bookId)
            }
        }
    }
}