package com.yareach.codesnaccbackend.util

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication

fun getUserId(auth: Authentication?): String? = when(auth) {
    is AnonymousAuthenticationToken -> null
    else -> auth?.name
}