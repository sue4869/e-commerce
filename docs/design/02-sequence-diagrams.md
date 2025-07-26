## 상품 목록 조회
```mermaid 
sequenceDiagram
    autonumber
    actor U as User
    participant PC as ProductController
    participant PS as ProductService
    participant PR as ProductRepository

    U ->> PC: GET /products?brand=1
    activate PC
    PC ->> PS: getProductsByBrandId
    activate PS
    PS ->> PR: getProductsByBrandId
    activate PR

    alt 브랜드에 해당하는 상품 존재
        PR -->> PS: 상품 목록 반환 (List<Product>)
    else 브랜드에 해당하는 상품 없음
        PR -->> PS: 빈 목록 반환 ([])
    end
    deactivate PR

    PS -->> PC: 응답
    deactivate PS

    PC -->> U: 200 OK + 상품 목록 또는 빈 리스트
    deactivate PC

```

## 상품 상세 조회
```mermaid 
sequenceDiagram
    autonumber
    actor U as User
    participant PC as ProductController
    participant PS as ProductService
    participant PR as ProductRepository

    U ->> PC: GET /products/{id}
    activate PC
    PC ->> PS: getProductById(id)
    activate PS
    PS ->> PR: findById(id)
    activate PR

    alt 상품 존재
        PR -->> PS: Product
    else 상품 없음
        PR -->> PS: null
    end

    deactivate PR
    alt 상품 존재
        PS -->> PC: 상품 정보 반환
    else 상품 없음
        PS -->> PC: NotFoundException 발생
    end
    deactivate PS

    alt 상품 존재
        PC -->> U: 200 OK + 상품 상세 정보
    else 상품 없음
        PC -->> U: 404 Not Found
    end
    deactivate PC
```

## 브랜드 상세 조회
```mermaid 
sequenceDiagram
    autonumber
    actor U as User
    participant BC as BrandController
    participant BS as BrandService
    participant BR as BrandRepository

    U ->> BC: GET /Brands/{id}
    activate BC
    BC ->> BS: getBrandById(id)
    activate BS
    BS ->> BR: findById(id)
    activate BR

    alt 브랜드 존재
        BR -->> BS: Brand
    else 브랜드 없음
        BR -->> BS: null
    end

    deactivate BR
    alt 브랜드 존재
        BS -->> BC: 브랜드 정보 반환
    else 브랜드 없음
        BS -->> BC: NotFoundException 발생
    end
    deactivate BS

    alt 브랜드 존재
        BC -->> U: 200 OK + 브랜드 상세 정보
    else 브랜드 없음
        BC -->> U: 404 Not Found
    end
    deactivate BC
```

## 상품 좋아요 등록
```mermaid 
sequenceDiagram
    autonumber
    actor U as User
    participant PC as ProductController
    participant US as UserService
    participant PF as ProductFacade
    participant PS as ProductService
    participant PR as ProductRepository
    participant LS as LikeService
    participant LR as LikeRepository

    U ->> PC: POST /products/{id}/likes
    activate PC
    PC ->> US: 사용자 인증 확인 (X-USER-ID)
    activate US
    
    alt 인증 실패 (헤더 누락 또는 사용자 없음)
        US -->> PC: 401 Unauthorized
    else 인증 성공
        US -->> PC: 사용자 정보 반환
        deactivate US
        PC ->> PS: 상품 조회 요청 (id)
        activate PS
        PS ->> PR: findById(id)
        activate PR

        alt 상품 존재
            PR -->> PS: Product
        else 상품 없음
            PR -->> PS: null
        end
        deactivate PR

        alt 상품 존재
            PS -->> PC: 상품 정보 반환
            deactivate PS
            PC ->> PF: 좋아요 등록 요청 (userId, productId)
            activate PF
            PF ->> LS: likeProduct(userId, productId)
            activate LS
            LS ->> LR: 사용자-상품 좋아요 여부 확인
            activate LR
            alt 이미 좋아요한 경우
                LR -->> LS: 이미 존재
            else 처음 좋아요
                LR -->> LS: 없음
                deactivate LR
                LS ->> LR: 좋아요 엔티티 저장
                activate LR
                LR -->> LS: 저장 완료
            end
            deactivate LR
            LS -->> PF: 저장 결과 반환, 레디스 저장
            deactivate LS
            PF -->> PC: 처리 결과 반환
        else 상품 없음
            PS -->> PC: NotFoundException
        end
        deactivate PF
    end
    
    alt 인증 실패 (헤더 누락 또는 사용자 없음)
        PC -->> U: 401 Unauthorized
    else 인증 성공 및 상품 존재
        alt 이미 좋아요한 경우
            PC -->> U: 200 OK (이미 좋아요됨)
        else 처음 좋아요
            PC -->> U: 201 Created (좋아요 완료)
        end
    else 인증 성공 및 상품 없음
        PC -->> U: 404 Not Found
    end
    deactivate PC   
```

## 상품 좋아요 취소
```mermaid 
sequenceDiagram
    autonumber
    actor U as User
    participant PC as ProductController
    participant US as UserService
    participant PF as ProductFacade
    participant PS as ProductService
    participant PR as ProductRepository
    participant LS as LikeService
    participant LR as LikeRepository

    U ->> PC: DELETE /products/{id}/likes
    activate PC
    PC ->> US: 사용자 인증 확인 (X-USER-ID)
    activate US
    
    alt 인증 실패 (헤더 누락 또는 사용자 없음)
        US -->> PC: 401 Unauthorized
        PC -->> U: 401 Unauthorized
    else 인증 성공
        US -->> PC: 사용자 정보 반환
        deactivate US
        PC ->> PS: 상품 조회 요청 (id)
        activate PS
        PS ->> PR: findById(id)
        activate PR

        alt 상품 존재
            PR -->> PS: Product
            deactivate PR
            PS -->> PC: 상품 정보 반환
            deactivate PS
            PC ->> PF: 좋아요 취소 요청 (userId, productId)
            activate PF
            PF ->> LS: cancelLike(userId, productId
            deactivate PF
            LS ->> LR: 좋아요 엔티티 존재 여부 확인
            activate LS
            alt 좋아요가 존재함
                LR -->> LS: 좋아요 엔티티 반환
                LS ->> LR: 좋아요 엔티티 삭제
                LR -->> LS: 삭제 완료
                LS -->> PF: 삭제 성공, 레디스 처리
                PF -->> PC: 처리 결과 반환
                PC -->> U: 200 OK (좋아요 취소 완료)
            else 좋아요가 존재하지 않음
                LR -->> LS: 없음
                LS -->> PF: 무시 or 처리 안함
                PF -->> PC: 처리 결과 반환
                PC -->> U: 200 OK (변경 없음)
            end
            deactivate LS

        else 상품 없음
            PR -->> PS: null
            PS -->> PC: NotFoundException
            PC -->> U: 404 Not Found
        end
    end
    deactivate PC
```

## 6. 주문 생성 및 결제 흐름
```mermaid 
sequenceDiagram
    autonumber
    actor U as User
    participant OC as OrderController
    participant US as UserService
    participant OS as OrderService
    participant PS as ProductService
    participant OR as OrderRepository
    participant SS as StockService
    participant PM as PaymentService
    participant PT as PointService
    participant EP as ExternalPaymentService

    U ->> OC: POST /api/v1/orders
    activate OC
    OC ->> US: 사용자 인증 확인 (X-USER-ID)
    activate US

    alt 인증 실패 또는 사용자 없음
        US -->> OC: 401 Unauthorized
        OC -->> U: 401 Unauthorized
    else 인증 성공
        US -->> OC: 사용자 정보 반환
        deactivate US
        OC ->> OS: 주문 생성 요청 (userId, orderItems, 사용 포인트)
        activate OS

        OS ->> PS: 상품 목록 조회
        activate PS
        PS -->> OS: 존재하는 상품 목록 반환
        deactivate PS

        OS ->> SS: 재고 존재 여부 확인
        activate SS
        alt 전부 품절
            SS -->> OS: 품절 상품 ID 목록
            OS -->> OC: 400 Bad Request (전부 품절)
            OC -->> U: 주문 불가
        else 일부 재고 있음
            SS -->> OS: 주문 가능 상품 목록 + 품절 상품 목록
        end
        deactivate SS

        OS ->> OR: 주문 저장 (대기 상태, 유효 상품 기준)
        activate OR
        OR -->> OS: 주문 ID 반환
        deactivate OR

        OS ->> PM: 결제 요청 (userId, orderId, 총 금액, 사용 포인트)
        activate PM

        PM ->> PT: 포인트 차감 요청
        activate PM
        alt 포인트 부족
            PT -->> PM: 포인트 차감 실패
            PM -->> OS: 결제 실패
            OS ->> OR: 주문 롤백
            OS -->> OC: 결제 실패 응답
            OC -->> U: 400 Bad Request (포인트 부족)
        else 포인트 차감 성공
            PT -->> PM: 포인트 차감 완료

            loop 외부 결제 재시도 최대 3회
                PM ->> EP: 외부 결제 요청 (남은 금액)
                alt 결제 성공
                    EP -->> PM: 결제 성공
                    PM -->> OS: 결제 성공

                    OS ->> SS: 재고 차감 서비스 요청
                    activate SS
                    alt 재고 차감 실패
                        SS -->> OS: 실패 상품 존재
                        OS ->> OR: 주문 롤백
                        OS ->> PT: 포인트 롤백
                        OS -->> OC: 재고 부족 응답
                        OC -->> U: 500 Internal Server Error (재고 부족)
                    else 차감 성공
                        SS -->> OS: 성공
                        OS ->> OR: 주문 상태 "결제완료"로 변경
                        OS -->> OC: 주문 생성 성공 응답 (품절 상품 포함 가능)
                        OC -->> U: 200 OK + 주문 정보
                    end
                    deactivate SS
                else 결제 실패
                    EP -->> PM: 결제 실패
                end
            end

            alt 3회 실패
                PM -->> OS: 결제 실패
                OS ->> OR: 주문 롤백
                OS ->> PT: 포인트 롤백
                OS -->> OC: 결제 실패 응답
                OC -->> U: 500 Internal Server Error (결제 실패)
            end
        end
        deactivate PM
    end
    deactivate OC


```