package loganalyzerbot.common

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.isDirectory


class FileChangeWatcher(
    private val path: String,
    private val fileExtension: String,
    private val callback: () -> Unit
) {
    private val watchService = FileSystems.getDefault().newWatchService()
    private val watchKeys = mutableMapOf<WatchKey, Path>()

    val run = true

    init {
        register(Path.of(path))

        Files.walkFileTree(Path.of(path), object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                if(file.isDirectory())
                   register(file)

                return FileVisitResult.CONTINUE
            }
        })
    }

    private fun register(path: Path) {
        val watchKey = path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
        watchKeys[watchKey] = path
    }

    fun watch() {
        GlobalScope.launch {
            while(run) {
                val key = watchService.take()
                if(containsChangeEvent(key)) {
                    callback()
                }

                if(!key.reset()) {
                    watchKeys.remove(key)
                }
            }
        }
    }

    private fun containsChangeEvent(key: WatchKey): Boolean {
        val dir = watchKeys[key] ?: return false

        for(event in key.pollEvents()) {
            val child = dir.resolve(event.context() as Path)

            when(event.kind()) {
                OVERFLOW -> continue
                ENTRY_CREATE -> {
                    if(Files.isDirectory(child)) {
                        register(child)
                    }
                }
            }

            if(child.fileName.toString().endsWith(fileExtension)) {
                return true
            }
        }
        return false
    }
}