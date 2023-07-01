package loganalyzerbot.logreader.dlt

import loganalyzerbot.logreader.LogMessage
import loganalyzerbot.logreader.LogReader
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.util.*

class DltReader(private val filter: DltFilter) : LogReader {
    private val parameterReader = DltParameterReader()
    private val stringBuilder = StringBuilder(100)
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

        val endHeaderOffset = fileInputStream.channel.position()
        val payloadSize = standardHeader.length -
                          (endHeaderOffset - startHeaderOffset - storageHeader.size)

        if(!extendedHeader.isLog() || !filter.filter(extendedHeader.applicationId, extendedHeader.contextId)) {
            fileInputStream.skip(payloadSize)
            return null
        }

        val payload = readDLTMessagePayload(inputStream,
                                            fileInputStream,
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
                                      fileInputStream: FileInputStream,
                                      storageHeader: DltStorageHeader,
                                      extendedHeader: DltExtendedHeader,
                                      payloadSize: Long): String {
        if (payloadSize <= 4L) {
            return ""
        }
        // Resetting the string reader is 2x faster than re-allocating it:
        // https://www.baeldung.com/java-clear-stringbuilder-stringbuffer
        stringBuilder.delete(0, stringBuilder.length)
        val payloadStartOffset = fileInputStream.channel.position()

        for (i in 0 until extendedHeader.numberOfArguments) {
            if(!parameterReader.appendParameter(inputStream, stringBuilder)) {
                // If we don't know the parameter type, we skip the rest of the payload.
                // This is a rare case and will become less likely as I keep implementing
                // new data types.
                val remainingPayload = payloadSize - (fileInputStream.channel.position() - payloadStartOffset)
                inputStream.skip(remainingPayload)
                return stringBuilder.toString()
            }
        }

        return stringBuilder.toString()
    }
}