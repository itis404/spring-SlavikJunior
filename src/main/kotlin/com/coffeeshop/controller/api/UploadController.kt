package com.coffeeshop.controller.api

import com.coffeeshop.service.FileStorageService
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/upload")
class UploadController(private val fileStorageService: FileStorageService) {

    @PostMapping("/menu-photo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('ADMIN')")
    fun uploadMenuPhoto(@RequestParam("file") file: MultipartFile): Map<String, String> {
        val url = fileStorageService.store(file, "menu")
        return mapOf("url" to url)
    }

    @PostMapping("/order-receipt", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("isAuthenticated()")
    fun uploadOrderReceipt(@RequestParam("file") file: MultipartFile): Map<String, String> {
        val url = fileStorageService.store(file, "receipts")
        return mapOf("url" to url)
    }
}
