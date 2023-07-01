package loganalyzerbot.logreader.dlt

import loganalyzerbot.common.read32BitLong
import loganalyzerbot.logreader.LogMessage
import loganalyzerbot.logreader.LogReader
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.util.*

class DltReader(private val filter: DltFilter) : LogReader {
    override fun read(dltFile: File): Array<LogMessage> {
        val fileInputStream = FileInputStream(dltFile)

        val inputStream = DataInputStream(fileInputStream)
        val dltMessages = readDLTMessages(fileInputStream, inputStream)
        inputStream.close()

        return dltMessages
    }

    private fun readDLTMessages(fileInputStream: FileInputStream,
                                inputStream: DataInputStream): Array<LogMessage> {
        val dltMessages = mutableListOf<LogMessage>()

        while (inputStream.available() > 0) {
            val dltMessage = readNextDLTMessage(fileInputStream, inputStream)

            if(dltMessage != null)
                dltMessages.add(dltMessage)
        }

        return dltMessages.toTypedArray()
    }

    private fun readNextDLTMessage(fileInputStream: FileInputStream,
                                   inputStream: DataInputStream): LogMessage? {
        if(!syncToDLTStorageHeader(inputStream)) {
            return null
        }

        // Offset of the start of the header (subtract the magic 'DLT' pattern)
        val startHeaderOffset = fileInputStream.channel.position() - 4

        val storageHeader = DltStorageHeader(inputStream)
        val standardHeader = DltStandardHeader(inputStream)
        val extraHeader = DltStandardHeaderExtra(inputStream, standardHeader)
        val extendedHeader = DltExtendedHeader(inputStream, standardHeader)

        val withEcuID = standardHeader.isWithECUId()
        val withSessionID = standardHeader.isWithSessionId()
        val withTimestamp = standardHeader.isWithTimestamp()

        val endHeaderOffset = fileInputStream.channel.position()
        val payloadSize = standardHeader.length -
                          (endHeaderOffset - startHeaderOffset - storageHeader.size)

        if(!extendedHeader.isLog() || !filter.filter(extendedHeader.applicationId, extendedHeader.contextId)) {
            fileInputStream.skip(payloadSize)
            return null
        }

        val payload = readDLTMessagePayload(inputStream,
                                            storageHeader,
                                            extendedHeader,
                                            payloadSize)

        if(payload.isEmpty())
            return null

        return LogMessage(payload,
                          Date(storageHeader.seconds * 1000 + storageHeader.microseconds),
                          extendedHeader.applicationId,
                          extendedHeader.contextId)
    }

    private fun syncToDLTStorageHeader(inputStream: DataInputStream): Boolean {
        // The following code searches for the 'DLT' magic pattern to find the
        // next log entry. This is more a workaround because DLT files contain
        // also other storage types (serial messages). As it is difficult to
        // find their specification, we use this workaround to skip them.
        while(inputStream.available() > 0) {
            if(inputStream.readByte().toUInt() == 0x44U)
                if(inputStream.readByte().toUInt() == 0x4CU)
                    if(inputStream.readByte().toUInt() == 0x54U)
                        if(inputStream.readByte().toUInt() == 0x01U)
                            return true
        }

        return false
    }

    private fun readDLTMessagePayload(inputStream: DataInputStream,
                                      storageHeader: DltStorageHeader,
                                      extendedHeader: DltExtendedHeader,
                                      payloadSize: Long): String {
        if (payloadSize <= 4L) {
            return ""
        }
        val stringBuilder = StringBuilder(100)
        var remainingPayload = payloadSize

        for (i in 0 until extendedHeader.numberOfArguments) {
            val typeInfo = inputStream.read32BitLong().toUInt()
            remainingPayload -= 4

            val isString = typeInfo shr 9 and 1U == 1U
            val isRaw = typeInfo shr 10 and 1U == 1U
            val containsVariableInfo = typeInfo shr 11 and 1U == 1U

            if (isString || isRaw) {
                // Why are the bytes here inverted ?!
                val dataLength = inputStream.readUnsignedByte().toUInt() +
                                 inputStream.readUnsignedByte().toUInt() * 256U
                remainingPayload -= 2

                if (containsVariableInfo) {
                    val variableInfoSize = inputStream.readUnsignedByte().toUInt() +
                                           inputStream.readUnsignedByte().toUInt() * 256U
                    remainingPayload -= 2

                    inputStream.skip(variableInfoSize.toLong())
                    remainingPayload -= variableInfoSize.toInt()
                }

                // Don't include the zero termination
                val data = ByteArray(dataLength.toInt() - 1)
                inputStream.readFully(data)
                inputStream.skip(1) // Skip the zero termination
                remainingPayload -= dataLength.toInt()

                stringBuilder.append(data.toString(Charsets.US_ASCII))
                if(i != 0)
                    stringBuilder.append(" ")
            } else {
                inputStream.skip(remainingPayload)
                return stringBuilder.toString()
            }
        }

        return stringBuilder.toString()
    }
}