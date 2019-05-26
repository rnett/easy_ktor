package com.rnett.easy_ktor

import io.ktor.sessions.SessionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.dumps
import kotlinx.serialization.loads

class KSerializerSerializer<T: Any>(val serializer: KSerializer<T>): SessionSerializer{
    override fun deserialize(text: String): T{
        return Cbor.plain.loads(serializer, text)
    }

    override fun serialize(session: Any): String {
        return Cbor.plain.dumps(serializer, session as T)
    }
}