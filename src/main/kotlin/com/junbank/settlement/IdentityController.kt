package com.junbank.settlement

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

// 워킹 스켈레톤: 자기 정체성만 반환한다(라우팅 실증용).
@RestController
class IdentityController {

    @GetMapping("/", produces = ["text/plain"])
    fun identity(): String = "I'm settlement server"
}
