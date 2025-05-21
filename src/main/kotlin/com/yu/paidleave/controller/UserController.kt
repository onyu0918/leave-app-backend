package com.yu.paidleave.controller


import com.yu.paidleave.entity.User
import com.yu.paidleave.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("/add")
    fun addUser(
        @RequestParam username: String,
        @RequestParam password: String,
        @RequestParam(defaultValue = "false") isAdmin: Boolean,
        @RequestParam joinDate: String
    ): ResponseEntity<ApiResponse<User>?> {
        val parsedDate = LocalDate.parse(joinDate)
        println("aaaaaa" + username)

        val response = ApiResponse(
            success = true,
            message = "사용자 추가 성공",
            data = userService.addUser(username, password, isAdmin, parsedDate)
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(response)
    }

    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        println(user)
        if (user == null) {
            val errorResponse = ApiResponse(
                success = false,
                message = "사용자 인증 실패",
                data = emptyMap<String, Any>()
            )
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                .body(errorResponse)
        }

        val user = userService.findUserByUsername(user.username)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                .body(ApiResponse(success = false, message = "사용자를 찾을 수 없습니다.", data = emptyMap()))
        println(user.joinDate.toString())
        val responseData: Map<String, Any> = mapOf(
            "username" to user.username,
            "joinDate" to user.joinDate.toString(),
            "isAdmin" to user.isAdmin
        )

        // 성공적인 응답 반환
        val response = ApiResponse(
            success = true,
            message = "현재 사용자 조회 성공",
            data = responseData
        )

//        return ResponseEntity.ok(response)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(response)
    }
}
