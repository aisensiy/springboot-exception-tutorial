package com.example.exception;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SpringBootApplication
@RestController
@RequestMapping("/")
public class ExceptionApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExceptionApplication.class, args);
  }

  @GetMapping("hello") public String getHello() {
    throw new IllegalStateException();
  }

  @PostMapping("hello")
  public Hello postHello(@RequestBody @Valid Hello hello) {
    return hello;
  }

}

class Hello {

  @NotBlank
  private String name;
  private int value;

  public String getName() {
    return name;
  }

  public int getValue() {
    return value;
  }
}

@RestControllerAdvice
class MyExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({IllegalStateException.class})
  protected ResponseEntity<Object> handleConflict(
      RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(ex, "error",
        new HttpHeaders(), HttpStatus.CONFLICT, request);
  }
}

@Component
class MyCustomErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(
      WebRequest webRequest, ErrorAttributeOptions errorAttributeOptions) {
    errorAttributeOptions = errorAttributeOptions.including(Include.EXCEPTION,
        Include.BINDING_ERRORS);
    Map<String, Object> errorAttributes =
        super.getErrorAttributes(webRequest, errorAttributeOptions);
    errorAttributes.put("locale", webRequest.getLocale()
        .toString());
    return errorAttributes;
  }
}