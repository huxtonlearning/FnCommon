package com.huxton.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionOm extends RuntimeException {
  HttpStatus status;
  String messageCode;

  public ExceptionOm(HttpStatus status, BaseErrorMessage msg) {
    super(msg.val());
    this.status = status;
    this.messageCode = msg.toString();
  }

  public ExceptionOm(HttpStatus status, BaseErrorMessage msg, String data) {
    super(msg.val() + "(" + data + ")");
    this.status = status;
    this.messageCode = msg.toString();
  }

  public ExceptionOm(HttpStatus status, String msg) {
    super(msg);
    this.status = status;
  }
}
