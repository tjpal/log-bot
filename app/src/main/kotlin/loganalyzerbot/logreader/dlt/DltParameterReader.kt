package loganalyzerbot.logreader.dlt

import loganalyzerbot.common.read32BitLong
import java.io.DataInputStream

/*
 Wraps reading a DLT parameter.

 The class does not encapsulate a parameter object but only a reader. The reason is that this code
 is executed in an inner loop and we want to avoid the costs of instantiation and re-allocating memory
 each time a parameter is read.
*/
class DltParameterReader {
    enum class TYPE {
        BOOL,
        SINT,
        UINT,
        FLOAT,
        ARRAY,
        STRING,
        RAWDATA,
        UNKNOWN
    }

    private var type: TYPE = TYPE.UNKNOWN
    private var containsVariableInfo = false

    fun appendParameter(inputStream: DataInputStream, stringBuilder: StringBuilder): Boolean {
        readTypeInfo(inputStream)

        when(type) {
            TYPE.STRING, TYPE.RAWDATA -> stringBuilder.append(readStringOrRawData(inputStream))
            else -> return false
        }

        return true
    }

    private fun readTypeInfo(inputStream: DataInputStream) {
        val typeInfo = inputStream.read32BitLong().toUInt()

        type = when(typeInfo and 0x3F8U) { // 0b1111111000
            0x8U -> TYPE.BOOL
            0x10U -> TYPE.SINT
            0x20U -> TYPE.UINT
            0x40U -> TYPE.FLOAT
            0x80U -> TYPE.ARRAY
            0x100U -> TYPE.STRING
            0x200U -> TYPE.RAWDATA
            else -> TYPE.UNKNOWN
        }

        containsVariableInfo = typeInfo shr 11 and 1U == 1U
    }

    private fun readStringOrRawData(inputStream: DataInputStream): String {
        val dataLength = inputStream.readUnsignedByte().toUInt() +
                         inputStream.readUnsignedByte().toUInt() * 256U

        if (containsVariableInfo) {
            val variableInfoSize = inputStream.readUnsignedByte().toUInt() +
                    inputStream.readUnsignedByte().toUInt() * 256U
            inputStream.skip(variableInfoSize.toLong())
        }

        // Don't include the zero termination
        val data = ByteArray(dataLength.toInt() - 1)
        inputStream.readFully(data)
        inputStream.skip(1) // Skip the zero termination

        return data.toString(Charsets.US_ASCII)
    }
}