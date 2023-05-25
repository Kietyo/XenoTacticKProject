package com.xenotactic.ecs

interface EntityComponentListener<T> {
    fun onAdd(newComponent: T) = Unit
    fun onReplace(oldComponent: T, newComponent: T) = Unit
    fun onAddOrReplace(oldComponent: T?, newComponent: T) = Unit
}