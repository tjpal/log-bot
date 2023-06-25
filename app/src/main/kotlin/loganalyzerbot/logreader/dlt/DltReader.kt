package loganalyzerbot.logreader.dlt

import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class DltReader {
    fun read(dltFile: File): Array<DltMessage> {
        val fileInputStream = FileInputStream(dltFile)

        val inputStream = DataInputStream(fileInputStream)
        val dltMessages = readDLTMessages(fileInputStream, inputStream)
        inputStream.close()

        return dltMessages
    }

    private fun readDLTMessages(fileInputStream: FileInputStream,
                                inputStream: DataInputStream): Array<DltMessage> {
        val dltMessages = mutableListOf<DltMessage>()

        while (inputStream.available() > 0) {
            dltMessages.add(readNextDLTMessage(fileInputStream, inputStream))
        }

        return dltMessages.toTypedArray()
    }

    private fun readNextDLTMessage(fileInputStream: FileInputStream,
                                   inputStream: DataInputStream): DltMessage {
        val startHeaderOffset = fileInputStream.channel.position()

        val storageHeader = DltStorageHeader(inputStream)
        if (!storageHeader.pattern.startsWith("DLT")) {
            throw Exception("Invalid DLT file")
        }

        val standardHeader = DltStandardHeader(inputStream)
        val extraHeader = DltStandardHeaderExtra(inputStream, standardHeader)
        val extendedHeader = DltExtendedHeader(inputStream, standardHeader)

        val endHeaderOffset = fileInputStream.channel.position()
        val isLog = extendedHeader.messageInfo.toInt() == 0

        val payload = readDLTMessagePayload(inputStream,
                                            standardHeader,
                                            storageHeader,
                                            (endHeaderOffset - startHeaderOffset).toInt(),
                                            isLog)

        return DltMessage(payload, storageHeader.seconds, isLog)
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