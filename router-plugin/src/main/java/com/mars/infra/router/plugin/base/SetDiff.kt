package com.mars.infra.router.plugin.base

import java.util.function.Consumer

/**
 * Created by JohnnySwordMan on 2022/1/14
 */
class SetDiff<T>(beforeList: Set<T>, afterList: Set<T>?) {

    private val addedList: MutableList<T> = ArrayList()
    private val unchangedList: MutableList<T> = ArrayList()
    private val removedList: MutableList<T> = ArrayList()

    fun getAddedList(): List<T> {
        return addedList
    }

    fun getUnchangedList(): List<T> {
        return unchangedList
    }

    fun getRemovedList(): List<T> {
        return removedList
    }

    init {
        addedList.addAll(afterList!!)
        beforeList.forEach(Consumer { t ->
            val result = if (addedList.remove(t)) unchangedList.add(t) else removedList.add(t)
        })
    }
}