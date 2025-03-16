package com.huxton.common.elk.enities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huxton.common.elk.enities.base.LogData;
import com.huxton.common.elk.enums.LogAction;
import com.huxton.common.elk.enums.LogStatus;
import lombok.Data;

@Data
public class LogObjectData extends LogData {
  @JsonProperty("user_id")
  private Long userId;

  private Integer version;

  @JsonProperty("object_name")
  private String objectName;

  @JsonProperty("object_id")
  private Long objectId;

  private LogAction action;
  private Object data;
  private LogStatus status;
}
