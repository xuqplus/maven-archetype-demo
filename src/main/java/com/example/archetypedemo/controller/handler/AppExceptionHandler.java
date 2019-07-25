package com.example.archetypedemo.controller.handler;

import com.example.archetypedemo.vo.resp.BasicResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler { // 捕获 Controller 内的异常

    @Autowired
    ContentNegotiationManager contentNegotiationManager;

    private ResponseEntity handleJson(HttpServletRequest request, String message) {
        return BasicResp.err(getStatus(request), message, null);
    }

    private ModelAndView handleHtml(HttpServletRequest request, String message) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("vo", handleJson(request, message).getBody());
        mav.setViewName("error");
        return mav;
    }

    private Object handle(String message) throws HttpMediaTypeNotAcceptableException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        List<MediaType> mediaTypes = contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
        if (mediaTypes.contains(MediaType.APPLICATION_JSON)) {
            return handleJson(request, message);
        }
        return handleHtml(request, message);
    }

    @ExceptionHandler(value = Throwable.class)
    public Object handle(Throwable t) throws HttpMediaTypeNotAcceptableException {
        log.error("t.message={}", t.getMessage());
        return handle(t.getMessage());
    }

    /**
     * copied from {@link AbstractErrorController}
     */
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
