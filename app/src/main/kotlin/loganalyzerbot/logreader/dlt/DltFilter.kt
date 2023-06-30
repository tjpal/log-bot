package loganalyzerbot.logreader.dlt

interface DltFilter {
    fun filter(appID: UInt, contextId: UInt): Boolean

    companion object {
        val DEFAULT = object: DltFilter {
            override fun filter(appID: UInt, contextId: UInt): Boolean {
                return true
            }
        }
    }
}