package ru.drudenko.weather.domain.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder({"code", "message"})
@JsonRootName("error")
@Getter
public class ApiException extends RuntimeException {

    @JsonProperty("code")
    private final int code;
    @JsonProperty("message")
    private String message;

    public ApiException() {
        this.code = HttpStatus.BAD_REQUEST.value();
    }

    public ApiException(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public ApiException(HttpStatus status, String message) {
        this.message = message;
        this.code = status.value();
    }

    public ApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public ApiException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.code = status.value();
        this.message = message;
    }

    public ApiException(int code, Throwable cause) {
        this(code, cause.getMessage(), cause);
    }

    public ApiException(int code) {
        this.code = code;
    }

}
