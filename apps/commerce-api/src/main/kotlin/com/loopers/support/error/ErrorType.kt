package com.loopers.support.error

import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val code: String, val message: String) {
    /** 범용 에러 */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, "일시적인 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.reasonPhrase, "존재하지 않는 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, HttpStatus.CONFLICT.reasonPhrase, "이미 존재하는 리소스입니다."),
    NOT_FOUND_USER_ID(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "존재하지 않는 사용자 ID 입니다."),
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, "외부 호출 에러입니다"),

    /**User**/
    INVALID_USER_ID_FORMAT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "회원 ID 는 6자 이상 10자 이내의 영문 및 숫자만 허용됩니다.",
    ),
    INVALID_USER_EMAIL_FORMAT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "이메일 형식은 'xx@yy.zz' 형식이어야합니다.",
    ),
    INVALID_USER_BIRTH_DAY_FORMAT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "생년월일 형식은 'yyyy-MM-dd' 형식이어야 합니다.",
    ),
    NOT_EXIST_USER(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "해당 회원이 존재하지 않습니다.",
    ),

    /**Product**/
    PRODUCT_NOT_FOUND(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "존재하지 않는 상품입니다",
    ),

    /**Charge**/
    CHARGE_AMOUNT_MUST_BE_POSITIVE(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "0 이하의 정수로 포인트를 충전할 수 없습니다.",
    ),
    NOT_ENOUGH_POINTS(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "포인트가 부족합니다.",
    ),
    INVALID_PAYMENT_TYPE(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "지원하지 않는 결제 수단입니다",
    ),
    INVALID_PAYMENT_PRICE(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "잘못된 결제금액입니다.",
    ),
    CONCURRENT_CONFLICT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "동시성 문제가 발생하였습니다.",
    ),

    /**Stock**/
    OUT_OF_STOCK(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "재고 부족",
    ),
    ONLY_AFTER_PAID(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "결제 이후에만 재고 차감할 수 있습니다.",
    ),

    /**Order**/
    QTY_MUST_BE_POSITIVE(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "0 이하의 정수인 QTY는 허용이 안됩니다.",
    ),
    PRICE_MUST_BE_POSITIVE(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "0 이하의 정수인 가격은 허용이 안됩니다.",
    ),
    INVALID_COUPON(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "존재하지 않는 쿠폰입니다.",
    ),
    USED_COUPON(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "이미 사용된 쿠폰입니다.",
    ),
    INVALID_DISCOUNT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "잘못된 할인 적용된 금액입니다.",
    ),
}
