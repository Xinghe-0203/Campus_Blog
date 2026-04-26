package com.example.edu_project.common.exception;

import com.example.edu_project.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.validation.ConstraintViolationException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：[{}]", e.getClass().getSimpleName());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常 (JPA/Spring 验证异常)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("参数校验失败");
        log.warn("约束违规异常：{}", message);
        return Result.error(400, message);
    }

    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = "请求参数 '" + e.getParameterName() + "' 缺失";
        log.warn("请求参数缺失：{}", message);
        return Result.error(400, message);
    }

    /**
     * 处理请求体解析失败异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = "请求体解析失败";
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException) {
            message = "参数类型不匹配：" + ((InvalidFormatException) cause).getValue();
        } else if (cause instanceof MismatchedInputException) {
            message = "请求体格式错误";
        }
        log.warn("请求体解析失败：[{}]", e.getClass().getSimpleName());
        return Result.error(400, message);
    }

    /**
     * 处理404资源不存在异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("资源不存在：{}", e.getRequestURL());
        return Result.error(404, "资源不存在");
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.warn("参数校验异常：{}", message);
        return Result.error(400, message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getFieldError() != null
                ? e.getFieldError().getDefaultMessage()
                : "参数绑定失败";
        log.warn("参数绑定异常：{}", message);
        return Result.error(400, message);
    }

    /**
     * 处理数据库重复键异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("数据重复：[{}]", e.getClass().getSimpleName());
        return Result.error(409, "数据已存在，操作冲突");
    }

    /**
     * 处理访问拒绝异常（权限不足）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("访问拒绝：{}", e.getMessage());
        return Result.error(403, "权限不足，拒绝访问");
    }

    /**
     * 处理认证异常（未登录或认证失败）
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败：{}", e.getMessage());
        return Result.error(401, "认证失败，请先登录");
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("文件上传超限：{}", e.getMessage());
        return Result.error(400, "上传文件大小超出限制");
    }

    /**
     * 最后的兜底异常处理
     */
    @ExceptionHandler(Throwable.class)
    public Result<Void> handleThrowable(Throwable e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error(500, "系统内部错误，请稍后重试");
    }
}
