package com.coffeeshop.service

import org.springframework.web.multipart.MultipartFile

interface FileStorageService {
    /** Сохраняет файл в подкаталог [subDir], возвращает публичный URL */
    fun store(file: MultipartFile, subDir: String): String

    /** Удаляет файл по его публичному URL */
    fun delete(publicUrl: String)
}
