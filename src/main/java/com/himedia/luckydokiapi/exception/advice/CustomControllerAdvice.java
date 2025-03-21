package com.himedia.luckydokiapi.exception.advice;


import com.himedia.luckydokiapi.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.security.auth.RefreshFailedException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * CustomControllerAdvice
 */
@Slf4j
@RestControllerAdvice
public class CustomControllerAdvice {


    /**
     * NoSuchElementException 발생시 처리
     *
     * @param e NoSuchElementException
     * @return ResponseEntity , Optional.isPresent(), Optional.get() 등에서 없을 때 발생!
     */
    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<?> notExist(NoSuchElementException e) {
        String msg = e.getMessage();
        log.error("NoSuchElementException: {}", msg);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage(msg)); // "msg": "No value present"
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<?> notExist(EntityNotFoundException e) {
        String msg = e.getMessage();
        log.error("EntityNotFoundException: {}", msg);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage(msg));
    }

    // 따라서, NoSuchElementException은 컬렉션이나 옵셔널에서 주로 사용되며,
    // EntityNotFoundException은 JPA와 관련된 엔티티 검색에서 사용되는 예외입니다. 두 예외는 서로 포함 관계가 없으며, 각각 별도의 상황에서 사용됩니다.

//  @ExceptionHandler(MethodArgumentNotValidException.class)
//  protected ResponseEntity<?> handleIllegalArgumentException(MethodArgumentNotValidException e) {
//
//      String msg = e.getMessage();
//
//      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("msg", msg));
//  }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {

        String msg = e.getMessage();
        log.error("IllegalArgumentException: {}", msg);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleIllegalArgumentException(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.error("handleIllegalArgumentException errors: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }




    /**
     * 컨트롤러 메소드의 매개변수 타입과 클라이언트가 전달한
     * 요청 값의 타입이 일치하지 않을 때 발생
     *
     * @param ex MethodArgumentTypeMismatchException
     * @return ResponseEntity
     */

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?>
    handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException
                                             ex) {
        String errorMessage = String.format(
                "Invalid argument: '%s'. Expected type: '%s'.",
                ex.getValue(),
                ex.getRequiredType().getSimpleName()
        );
        log.error("MethodArgumentTypeMismatchException: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(errorMessage));
    }

    /**
     * 요청 바디가 JSON 형식이 아닐 때 발생
     *
     * @param e HttpMessageNotReadableException
     * @return ResponseEntity
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?>
    handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        String msg = e.getMessage();
        log.error("HttpMessageNotReadableException: {}", msg);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(msg));
    }

    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJWTException(CustomJWTException e) {

        String msg = e.getMessage();
        log.error("CustomJWTException: {}", msg);

        return ResponseEntity.ok().body(getErrorMessage(msg));
    }

    @ExceptionHandler(OutOfStockException.class)
    protected ResponseEntity<?> handleOutOfStockException(OutOfStockException e) {

        String msg = e.getMessage();
        log.error("OutOfStockException: {}", msg);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(msg));
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {

        String msg = e.getMessage();
        log.error("UsernameNotFoundException: {}", msg);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(msg));
    }

    // url path 가 틀려서 나오는 exception NoHandlerFoundException
    // 404 에러 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        String msg = e.getMessage();
        log.error("NoHandlerFoundException: {}", msg);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage(msg));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<?> handleEventNotFoundException(EventNotFoundException e) {
        String msg = e.getMessage();
        log.error("EventNotFoundException: {}", msg);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage(msg));
    }

    /**
     * 엑셀 파일 업로드 실패시 처리
     *
     * @param e ExcelFailException
     * @return ResponseEntity
     */
    @ExceptionHandler(OrderMemberNotFoundException.class)
    public ResponseEntity<?> handleOrderNotFoundException(OrderMemberNotFoundException e) {
        String msg = e.getMessage();
        log.error("OrderMemberNotFoundException: {}", msg);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage(msg));
    }


    @ExceptionHandler(NotAccessChatRoom.class)
    public ResponseEntity<?> NotAccessChattingRoom(NotAccessChatRoom e) {
        String msg = e.getMessage();
        log.error("NotAccessChattingRoom: {}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(msg));
    }

    @ExceptionHandler(NotSellerAccessException.class)
    public ResponseEntity<?> NotAccessSeller(NotSellerAccessException e) {
        String msg = e.getMessage();
        log.error("NotSellerAccessException: {}", msg);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage(msg));
    }

    @ExceptionHandler(ExcelFailException.class)
    public ResponseEntity<?> handleExcelFailException(ExcelFailException e) {
        String msg = e.getMessage();
        log.error("handleExcelFailException: {}", msg);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(msg));
    }

    // refreshToken 문제 발생시 exception
    @ExceptionHandler(RefreshFailedException.class)
    public ResponseEntity<?> handleRefreshTokenExpiredException(RefreshFailedException e) {
        String msg = e.getMessage();
        log.error("RefreshFailedException: {}", msg);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getErrorMessage(msg));
    }

    // coupon Lock exception
    @ExceptionHandler(CouponLockException.class)
    public ResponseEntity<?> handleCouponLockException(CouponLockException e) {
        String msg = e.getMessage();
        log.error("CouponLockException: {}", msg);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrorMessage(msg));
    }

    /**
     * 나머지 exception 서버오류 500 에러로 통일
     *
     * @param e 글로벌 Exception
     * @return ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        String msg = e.getMessage();
        log.error("handleGeneralException: {}", msg);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorMessage(e.getMessage()));
    }

    private static Map<String, String> getErrorMessage(String msg) {
        return Map.of("error", msg);
    }

}
