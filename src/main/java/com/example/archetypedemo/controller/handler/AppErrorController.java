package com.example.archetypedemo.controller.handler;

import com.example.archetypedemo.vo.resp.BasicResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * 全局错误控制器
 * <p>
 * {@link BasicErrorController }中定义了 text/html 和 *\/* 的处理方法
 * <p>
 * 如果需要可以继承{@link ErrorController}自行实现
 */
@RestController
@RequestMapping(AppErrorController.ERROR_PATH)
public class AppErrorController implements ErrorController {

    public static final String ERROR_PATH = "/error"; // do not change
    public static final boolean INCLUDE_STACK_TRACE = false;

    @Value("${server.error.path:${error.path:/error}}")
    String aaa;

    @Autowired
    ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity jsonError(HttpServletRequest request) {
        Map<String, Object> attributes = Collections.unmodifiableMap(
                this.errorAttributes.getErrorAttributes(new ServletWebRequest(request), INCLUDE_STACK_TRACE));
        String message = (String) attributes.get("message");
        if ("No message available".equals(message)) {
            Object error = attributes.get("error");
            if (null != error && error instanceof String) {
                message = (String) error;
            }
        }
        return BasicResp.err(getStatus(request), message, attributes);
    }

    @RequestMapping
    public ModelAndView otherError(HttpServletRequest request, ModelAndView mav) {
        ResponseEntity responseEntity = this.jsonError(request);
        mav.addObject("vo", responseEntity.getBody());
        mav.setViewName("error");
        return mav;
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
