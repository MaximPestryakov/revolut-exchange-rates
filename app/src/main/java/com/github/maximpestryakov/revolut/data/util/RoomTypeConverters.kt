package com.github.maximpestryakov.revolut.data.util

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.Date

object RoomTypeConverters {

    private const val BIG_DECIMAL_SCALE = 6

    @JvmStatic
    @TypeConverter
    fun dateToLong(date: Date) = date.time

    @JvmStatic
    @TypeConverter
    fun dateFromLong(long: Long) = Date(long)

    @JvmStatic
    @TypeConverter
    fun bigDecimalToString(bigDecimal: BigDecimal) = bigDecimal.toString()

    @JvmStatic
    @TypeConverter
    fun bigDecimalFromString(string: String): BigDecimal = BigDecimal(string).setScale(BIG_DECIMAL_SCALE)
}
