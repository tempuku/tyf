package queue.file.modified

import config.initConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.engine.spec.tempdir
import java.io.File
import java.nio.file.Paths

class FileModifiedConsumerTest : FunSpec({
//    val testDir = Paths.get("").toAbsolutePath().toString() + "/src/test"
//    val tempDir = tempdir().toString()
//    val pdf1 = "$testDir/pdf1.pdf";
//    val userHome = System.getProperty("user.home")
//    val config = initConfig(tempDir)
//    val fileModifiedConsumer = FileModifiedConsumer(config.fileUpdateQueue)
//
//    test("consume") {
//        val payload = FileModifiedPayload(pdf1, 0)
//        val result = fileModifiedConsumer.consume(payload)
//        result.size shouldBe 3
//        File("$tempDir/.tracker/image").listFiles()?.size shouldBe 1
//    }
})
