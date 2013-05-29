package ssotest.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedRequestException extends RuntimeException {
    private static final long serialVersionUID = 5176795717882839631L;

    public UnauthorizedRequestException() {
        super();
    }
}
