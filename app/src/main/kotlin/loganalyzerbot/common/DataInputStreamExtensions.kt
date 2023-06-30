package loganalyzerbot.common

import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun DataInputStream.read32BitLong(): Long {
    val bytes = ByteArray(4)
    this.readFully(bytes)
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).int.toLong()
}

fun DataInputStream.readUInt(): UInt {
    val bytes = ByteArray(4)
    this.readFully(bytes)
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).int.toUInt()
}

fun DataInputStream.readString(numBytes: Int): String {
    val bytes = ByteArray(numBytes)
    this.readFully(bytes)
    return bytes.toString(Charsets.US_ASCII)
}

fun DataInputStream.read16BitInt(): Int {
    return (this.readUnsignedByte().toUInt() * 256U +
            this.readUnsignedByte().toUInt()).toInt()
}