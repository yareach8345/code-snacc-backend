package com.yareach.codesnaccbackend.exception

class RequiredFieldIsNullException(
    fieldName: String,
    className: String
): RuntimeException("필수 필드가 Null입니다: $fieldName in $className")