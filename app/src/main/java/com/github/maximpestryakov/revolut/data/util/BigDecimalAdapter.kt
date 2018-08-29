package com.github.maximpestryakov.revolut.data.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.math.BigDecimal

internal class BigDecimalAdapter : JsonAdapter<BigDecimal>() {

    override fun fromJson(reader: JsonReader): BigDecimal? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull()
        }
        return BigDecimal(reader.nextString()).setScale(BIG_DECIMAL_SCALE)
    }

    override fun toJson(writer: JsonWriter, value: BigDecimal?) {
        if (value != null) {
            writer.value(value.toPlainString())
        }
    }

    companion object {
        private const val BIG_DECIMAL_SCALE = 6
    }
}
