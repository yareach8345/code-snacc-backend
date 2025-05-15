package com.yareach.codesnaccbackend.exception

class NotSupportSearchOptionException(
    searchOption: String
): RuntimeException("지원하지 않는 검색입니다: $searchOption")