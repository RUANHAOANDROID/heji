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
 *@author: 锅得铁
 *# entity 为 @Entity类型
 */
class EventMessage(var crud: SyncEvent, var entity: Any)


enum class SyncEvent {

    ADD, PULL, UPDATE, DELETE
}


object DataBus {
    fun post(eventMessage: EventMessage) {
        AppViewModel.get().localDataEvent.postValue(eventMessage)
    }

    fun post(crud: SyncEvent, entity: Any) {
        AppViewModel.get().localDataEvent.postValue(EventMessage(crud, entity))
    }

    fun subscriberForever(observer: (EventMessage) -> Unit) {
        AppViewModel.get().localDataEvent.observeForever(observer)
    }

    fun subscriber(lifecycleOwner: LifecycleOwner, observer: (EventMessage) -> Unit) {
        AppViewModel.get().localDataEvent.observe(lifecycleOwner, observer)
    }

}

class BookTask(private val crud: SyncEvent, val book: Book) {
    private val bookRepository = BookRepository()
    suspend fun sync() {
        when (crud) {
            SyncEvent.DELETE -> {
                bookRepository.deleteBook(book_id = book.id)
            }
            SyncEvent.PULL -> {
                "do nothing"
            }
            SyncEvent.UPDATE -> {
                bookRepository.updateBook(book)
            }
            SyncEvent.ADD -> {
                bookRepository.addBook(book)
            }
        }
    }
}

class BillTask(private val crud: SyncEvent, val bill: Bill) {
    private val billRepository = BillRepository()
    suspend fun sync() {
        when (crud) {
            SyncEvent.DELETE -> {
                billRepository.deleteBill(_id = bill.id)
            }
            SyncEvent.PULL -> {
                "do nothing"
            }
            SyncEvent.UPDATE -> {
                billRepository.updateBill(bill)
            }
            SyncEvent.ADD -> {
                billRepository.addBill(bill)
            }
        }
    }
}

class CategoryTask(private val crud: SyncEvent, val category: Category) {
    private val categoryRepository = CategoryRepository()
    suspend fun sync() {
        when (crud) {
            SyncEvent.DELETE -> {
                categoryRepository.deleteCategory(_id = category.id)
            }
            SyncEvent.PULL -> {
                "do nothing"
            }
            SyncEvent.UPDATE -> {
                categoryRepository.updateCategory(category)
            }
            SyncEvent.ADD -> {
                categoryRepository.addCategory(CategoryEntity(category), category.bookId)
            }
        }
    }
}