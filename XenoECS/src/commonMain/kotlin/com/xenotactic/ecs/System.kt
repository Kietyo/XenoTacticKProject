package com.xenotactic.ecs

import kotlin.time.Duration
import kotlin.time.DurationUnit

abstract class System {
    var isEnabled = true

    abstract val familyConfiguration: FamilyConfiguration
    private var family: Family = Family.EMPTY
    internal fun setFamily(newFamily: Family) {
        family = newFamily
    }
    fun getFamily() = family

    abstract fun update(deltaTime: Duration)
}