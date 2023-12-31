package loganalyzerbot.logreader.dlt

import loganalyzerbot.common.read16BitInt
import loganalyzerbot.common.read32BitLong
import loganalyzerbot.common.readString
import loganalyzerbot.common.readUInt
import java.io.DataInputStream

class DltStorageHeader(var seconds: Long,
                       var microseconds: Long,
                       var ecuId: String) {
    constructor(inputStream: DataInputStream) : this(
        inputStream.read32BitLong(),
        inputStream.read32BitLong(),
        inputStream.readString(4)
    )

    val size = 16
}

class DltStandardHeader(private var headerTyp: Int, var messageCounter: Byte, var length: Int) {
    constructor(inputStream: DataInputStream) : this(
        inputStream.readByte().toInt(),
        inputStream.readByte(),
        inputStream.read16BitInt()
    )

    fun isExtendedHeaderUsed(): Boolean = headerTyp and 1 == 1
    fun isWithECUId(): Boolean = headerTyp shr 2 and 1 == 1
    fun isWithSessionId(): Boolean = headerTyp shr 3 and 1 == 1
    fun isWithTimestamp(): Boolean = headerTyp shr 4 and 1 == 1
}

class DltStandardHeaderExtra(var ecu: String, var sessionNumber: Long, var timestamp: Long) {
    constructor(inputStream: DataInputStream, standardHeader: DltStandardHeader) : this(
        if(standardHeader.isWithECUId()) inputStream.readString(4) else "",
        if(standardHeader.isWithSessionId()) inputStream.read32BitLong() else 0,
        if(standardHeader.isWithTimestamp()) inputStream.read32BitLong() else 0
    )
}

class DltExtendedHeader(var messageInfo: Byte, var numberOfArguments: Byte, var applicationId: UInt, var contextId: UInt) {
    constructor(inputStream: DataInputStream, standardHeader: DltStandardHeader) : this(
        if(standardHeader.isExtendedHeaderUsed()) inputStream.readByte() else 0,
        if(standardHeader.isExtendedHeaderUsed()) inputStream.readByte() else 0,
        if(standardHeader.isExtendedHeaderUsed()) inputStream.readUInt() else 0u,
        if(standardHeader.isExtendedHeaderUsed()) inputStream.readUInt() else 0u,
    )

    fun isLog() = messageInfo.toUInt() and 0x0EU == 0U
}