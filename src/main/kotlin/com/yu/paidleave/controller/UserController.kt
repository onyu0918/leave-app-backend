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
        @RequestParam name: String,
        @RequestParam(defaultValue = "false") isAdmin: Boolean,
        @RequestParam joinDate: String
    ): ResponseEntity<ApiResponse<User>?> {
        val parsedDate = LocalDate.parse(joinDate)

        val response = ApiResponse(
            success = true,
            message = "ユーザーが正常に追加されました。",
            data = userService.addUser(username, password, name, isAdmin, parsedDate)
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(response)
    }

    @DeleteMapping("/user/{username}")
    fun deleteUser(
        @PathVariable username: String,
        @AuthenticationPrincipal admin: User?
    ): ResponseEntity<ApiResponse<Nothing>> {
        if (admin == null || !admin.isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse(false, "管理者のみが操作できます。", null))
        }

        val success = userService.deleteUserByUsername(username)
        return if (success) {
            ResponseEntity.ok(ApiResponse(true, "ユーザーを削除しました。", null))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(false, "該当ユーザーが存在しません。", null))
        }
    }

    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        println(user)
        if (user == null) {
            val errorResponse = ApiResponse(
                success = false,
                message = "ユーザーの認証に失敗しました。もう一度お試しください。",
                data = emptyMap<String, Any>()
            )
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                .body(errorResponse)
        }

        val user = userService.findUserByUsername(user.username)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                .body(ApiResponse(success = false, message = "該当するユーザーが見つかりませんでした。", data = emptyMap()))
        println(user.joinDate.toString())
        val responseData: Map<String, Any> = mapOf(
            "username" to user.username,
            "name" to user.name,
            "joinDate" to user.joinDate.toString(),
            "isAdmin" to user.isAdmin
        )

        val response = ApiResponse(
            success = true,
            message = "現在のユーザー情報の取得に成功しました。",
            data = responseData
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(response)
    }

    @GetMapping("/user/{username}")
    fun getUserByUsername(
        @PathVariable username: String
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val user = userService.findUserByUsername(username)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                .body(ApiResponse(success = false, message = "該当するユーザーが見つかりませんでした。", data = emptyMap()))

        val responseData: Map<String, Any> = mapOf(
            "username" to user.username,
            "name" to user.name,
            "joinDate" to user.joinDate.toString(),
            "isAdmin" to user.isAdmin
        )

        val response = ApiResponse(
            success = true,
            message = "ユーザー情報の取得に成功しました。",
            data = responseData
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(response)
    }

    @PutMapping("/change-password")
    fun changePassword(
        @AuthenticationPrincipal user: User?,
        @RequestParam username: String,
        @RequestParam newPassword: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse(false, "認証に失敗しました。", null))
        }

        val success = userService.changePassword(username, newPassword)
        return if (success) {
            ResponseEntity.ok(ApiResponse(true, "パスワードを変更しました。", null))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse(false, "現在のパスワードが正しくありません。", null))
        }
    }

    @PutMapping("/reset-password/{username}")
    fun resetPassword(
        @PathVariable username: String,
        @AuthenticationPrincipal admin: User?
    ): ResponseEntity<ApiResponse<Nothing>> {
        if (admin == null || !admin.isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse(false, "管理者のみが操作できます。", null))
        }

        val success = userService.resetPasswordToDefault(username)
        return if (success) {
            ResponseEntity.ok(ApiResponse(true, "パスワードを '1234' に初期化しました。", null))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(false, "該当ユーザーが存在しません。", null))
        }
    }

    @GetMapping("/all")
    fun getAllUsers(
        @AuthenticationPrincipal admin: User?
    ): ResponseEntity<ApiResponse<List<Map<String, Any>>>> {
        if (admin == null || !admin.isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse(false, "管理者のみが操作できます。", null))
        }

        val users = userService.findAllUsers()

        val userList: List<Map<String, Any>> = users.map {
            mapOf<String, Any>(
                "username" to it.username,
                "name" to it.name,
                "joinDate" to it.joinDate.toString(),
                "isAdmin" to it.isAdmin
            )
        }
        val response = ApiResponse(
            success = true,
            message = "全ユーザー情報の取得に成功しました。",
            data = userList
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(response)
    }
}
