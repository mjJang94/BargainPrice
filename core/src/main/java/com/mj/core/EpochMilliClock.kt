package com.mj.core

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class EpochMilliClock(
    private val millis: Long = System.currentTimeMillis(),
    private val zone: ZoneId = ZoneId.systemDefault(),
) : Clock() {
    override fun getZone(): ZoneId = zone

    override fun withZone(zone: ZoneId): Clock {
        if (zone == this.zone) return this
        return EpochMilliClock(millis, zone)
    }

    override fun millis(): Long = millis

    override fun instant(): Instant =
        Instant.ofEpochMilli(millis())

    override fun equals(other: Any?): Boolean =
        other is EpochMilliClock && millis == other.millis && zone == other.zone

    override fun hashCode(): Int =
        zone.hashCode() xor millis.hashCode()

    override fun toString(): String =
        "EpochMillClock[$millis, $zone]"
}
