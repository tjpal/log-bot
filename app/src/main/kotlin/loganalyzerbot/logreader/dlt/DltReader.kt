package loganalyzerbot.logreader.dlt

import loganalyzerbot.logreader.LogMessage
import loganalyzerbot.logreader.LogReader
import loganalyzerbot.logreader.LogType
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.util.*

class DltReader : LogReader {
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
                                   inputStream: DataInputStream): LogMessage {
        if(!syncToDLTStorageHeader(inputStream)) {
            return null
        }

        // Offset of the start of the header (subtract the magic 'DLT' pattern)
        val startHeaderOffset = fileInputStream.channel.position() - 4

        val storageHeader = DltStorageHeader(inputStream)
        val standardHeader = DltStandardHeader(inputStream)
        val extraHeader = DltStandardHeaderExtra(inputStream, standardHeader)
        val extendedHeader = DltExtendedHeader(inputStream, standardHeader)

        val endHeaderOffset = fileInputStream.channel.position()
        val isLog = extendedHeader.messageInfo.toInt() == 0 || extendedHeader.messageInfo.toInt() == 2

        val payload = readDLTMessagePayload(inputStream,
                                            standardHeader,
                                            storageHeader,
                                            (endHeaderOffset - startHeaderOffset).toInt(),
                                            isLog)

        return LogMessage(payload,
                          Date(storageHeader.seconds * 1000 + storageHeader.microseconds),
                          extendedHeader.applicationId,
                          extendedHeader.contextId,
                          if(isLog) LogType.LOG else LogType.CONTROL)
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
                                      standardHeader: DltStandardHeader,
                                      storageHeader: DltStorageHeader,
                                      readHeaderSize: Int,
                                      isLog: Boolean): String {
        var payloadSize =
            (standardHeader.length + storageHeader.size - readHeaderSize).toInt()

        if (payloadSize == 0) {
            return ""
        }

        if (!isLog) {
            inputStream.skip(payloadSize.toLong())
            return ""
        }

        // Skip some magic header
        val header = ByteArray(10)
        inputStream.readFully(header)

        val payload = ByteArray(payloadSize - 10)
        inputStream.readFully(payload)

        // Skip the trailing zeros in the binary format of the message
        var trailingZeros = 0
        while (payload.size - trailingZeros >= 0 && payload[payload.size - trailingZeros - 1] == 0.toByte()) {
            trailingZeros++
        }

        val payloadAsString =
            payload.sliceArray(0 until payload.size - trailingZeros).toString(Charsets.UTF_8)

       return payloadAsString
    }
}