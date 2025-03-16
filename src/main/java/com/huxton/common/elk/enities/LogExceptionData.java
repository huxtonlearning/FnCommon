package com.huxton.common.elk.enities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huxton.common.elk.enities.base.LogData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class LogExceptionData extends LogData {
  private Integer status;
  private Date timestamp;
  private String message;

  @JsonProperty("message_code")
  private String messageCode;

  private String description;
  private String path;
  private Object params;
  private Object body;
  private Object headers;
}
