package com.coffeeshop.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(CoffeeShopException::class)
    fun handleCoffeeShopException(
        ex: CoffeeShopException,
        request: HttpServletRequest,
    ): Any {
        val logMessage = (ex as? EntityNotFoundException)?.detail ?: ex.message
        log.error("CoffeeShopException [${ex.httpStatus}] on ${request.requestURI}: $logMessage")
        return if (request.requestURI.startsWith("/api/")) {
            ResponseEntity.status(ex.httpStatus).body(ErrorResponse(ex.httpStatus.value(), ex.message ?: "Error"))
        } else {
            val mav = ModelAndView("error/general")
            mav.addObject("status", ex.httpStatus.value())
            mav.addObject("error", ex.message)
            mav
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors.joinToString("; ") {
            "${it.field}: ${it.defaultMessage}"
        }
        log.warn("Validation error on ${request.requestURI}: $message")
        return ResponseEntity.badRequest().body(ErrorResponse(400, message))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val message = "Invalid value '${ex.value}' for parameter '${ex.name}'"
        log.warn("Type mismatch on ${request.requestURI}: $message")
        return ResponseEntity.badRequest().body(ErrorResponse(400, message))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFound(
        ex: NoHandlerFoundException,
        request: HttpServletRequest,
    ): Any {
        return if (request.requestURI.startsWith("/api/")) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(404, "Resource not found"))
        } else {
            val mav = ModelAndView("error/general")
            mav.status = HttpStatus.NOT_FOUND
            mav.addObject("status", 404)
            mav.addObject("error", "Страница не найдена")
            mav
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ): Any {
        log.warn("Malformed request body on ${request.requestURI}: ${ex.message}")
        return if (request.requestURI.startsWith("/api/")) {
            ResponseEntity.badRequest().body(ErrorResponse(400, "Malformed or missing request body"))
        } else {
            val mav = ModelAndView("error/general")
            mav.addObject("status", 400)
            mav.addObject("error", "Bad request")
            mav
        }
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest,
    ): Any {
        log.warn("IllegalArgumentException on ${request.requestURI}: ${ex.message}")
        return if (request.requestURI.startsWith("/api/")) {
            ResponseEntity.badRequest().body(ErrorResponse(400, ex.message ?: "Bad request"))
        } else {
            val mav = ModelAndView("error/general")
            mav.addObject("status", 400)
            mav.addObject("error", ex.message ?: "Bad request")
            mav
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        ex: Exception,
        request: HttpServletRequest,
    ): Any {
        log.error("Unexpected error on ${request.requestURI}", ex)
        return if (request.requestURI.startsWith("/api/")) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(500, "Internal server error"))
        } else {
            val mav = ModelAndView("error/general")
            mav.addObject("status", 500)
            mav.addObject("error", "Internal server error")
            mav
        }
    }
}
