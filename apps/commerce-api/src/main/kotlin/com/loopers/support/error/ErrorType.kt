package com.loopers.support.error

import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val code: String, val message: String) {
    /** 범용 에러 */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, "일시적인 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.reasonPhrase, "존재하지 않는 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, HttpStatus.CONFLICT.reasonPhrase, "이미 존재하는 리소스입니다."),
    NOT_FOUND_USER_ID(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "존재하지 않는 사용자 ID 입니다."),

    /**User**/
    INVALID_USER_ID_FORMAT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "회원 ID 는 6자 이상 10자 이내의 영문 및 숫자만 허용됩니다.",
    ),
    INVALID_USER_EMAIL_FORMAT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "이메일 형식은 'xx@yy.zz' 형식이어야합니다."),
    INVALID_USER_BIRTH_DAY_FORMAT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "생년월일 형식은 'yyyy-MM-dd' 형식이어야 합니다.",
    ),
    NOT_EXIST_USER(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "해당 회원이 존재하지 않습니다."
    ),

    /**Charge**/
    CHARGE_AMOUNT_MUST_BE_POSITIVE(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "0 이하의 정수로 포인트를 충전할 수 없습니다."
    )
}
