package com.coffeeshop.service.impl

import com.coffeeshop.config.AppProperties
import com.coffeeshop.service.FileStorageService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@Service
class FileStorageServiceImpl(private val props: AppProperties) : FileStorageService {

    private val contentTypeToExt = mapOf(
        "image/jpeg" to "jpg",
        "image/png"  to "png",
        "image/webp" to "webp",
    )

    override fun store(file: MultipartFile, subDir: String): String {
        // HIGH-3: validate subDir to prevent path traversal via directory name
        require(subDir.matches(Regex("^[a-zA-Z0-9_-]+$"))) { "Invalid subDir" }

        // HIGH-2: derive extension from Content-Type, never from filename
        val contentType = file.contentType
        val ext = contentTypeToExt[contentType]
            ?: throw IllegalArgumentException(
                "Разрешены только изображения JPEG, PNG и WebP (получен: $contentType)"
            )

        require(file.size <= props.uploads.maxFileSizeBytes) {
            "Размер файла не должен превышать ${props.uploads.maxFileSizeBytes / (1024 * 1024)} МБ"
        }

        // HIGH-2: magic-byte verification — read bytes once and reuse
        val fileBytes = file.bytes
        verifyMagicBytes(fileBytes, contentType)

        val filename = "${UUID.randomUUID()}.$ext"

        val uploadsRoot = Paths.get(props.uploads.dir).normalize().toAbsolutePath()
        val targetDir = uploadsRoot.resolve(subDir).normalize().toAbsolutePath()

        // HIGH-3: ensure targetDir cannot escape the uploads root
        require(targetDir.startsWith(uploadsRoot)) { "targetDir escapes uploads root" }

        Files.createDirectories(targetDir)
        Files.write(targetDir.resolve(filename), fileBytes)

        return "${props.uploads.baseUrl.trimEnd('/')}/$subDir/$filename"
    }

    override fun delete(publicUrl: String) {
        val relative = publicUrl.removePrefix(props.uploads.baseUrl).trimStart('/')
        val path = Paths.get(props.uploads.dir).resolve(relative).normalize()
        // Проверка, что путь внутри uploads dir (защита от path traversal)
        val uploadsRoot = Paths.get(props.uploads.dir).normalize()
        require(path.startsWith(uploadsRoot)) { "Недопустимый путь файла" }
        Files.deleteIfExists(path)
    }

    /**
     * Validates file magic bytes against known signatures.
     * JPEG: FF D8 FF
     * PNG:  89 50 4E 47 0D 0A 1A 0A
     * WebP: 52 49 46 46 ?? ?? ?? ?? 57 45 42 50  (RIFF....WEBP)
     */
    private fun verifyMagicBytes(bytes: ByteArray, contentType: String?) {
        require(bytes.size >= 12) { "File too small to determine type" }
        val valid = when (contentType) {
            "image/jpeg" ->
                bytes[0] == 0xFF.toByte() &&
                bytes[1] == 0xD8.toByte() &&
                bytes[2] == 0xFF.toByte()

            "image/png" ->
                bytes[0] == 0x89.toByte() &&
                bytes[1] == 0x50.toByte() &&
                bytes[2] == 0x4E.toByte() &&
                bytes[3] == 0x47.toByte() &&
                bytes[4] == 0x0D.toByte() &&
                bytes[5] == 0x0A.toByte() &&
                bytes[6] == 0x1A.toByte() &&
                bytes[7] == 0x0A.toByte()

            "image/webp" ->
                bytes[0] == 0x52.toByte() &&   // R
                bytes[1] == 0x49.toByte() &&   // I
                bytes[2] == 0x46.toByte() &&   // F
                bytes[3] == 0x46.toByte() &&   // F
                bytes[8] == 0x57.toByte() &&   // W
                bytes[9] == 0x45.toByte() &&   // E
                bytes[10] == 0x42.toByte() &&  // B
                bytes[11] == 0x50.toByte()     // P

            else -> false
        }
        require(valid) { "Содержимое файла не соответствует заявленному типу $contentType" }
    }
}
