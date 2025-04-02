package com.example.todolist

import android.content.Context
import io.objectbox.BoxStore

object ObjectBox {
    private lateinit var boxStore: BoxStore

    fun init(context: Context) {
        if (!ObjectBox::boxStore.isInitialized) {
            boxStore = MyObjectBox.builder().androidContext(context.applicationContext).build()
        }
    }

    val store: BoxStore
        get() = boxStore
}
