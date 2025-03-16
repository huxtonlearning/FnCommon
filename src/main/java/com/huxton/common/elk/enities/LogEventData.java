package com.huxton.common.elk.enities;

import com.huxton.common.elk.enities.base.LogData;
import com.huxton.common.elk.enums.LogAction;
import lombok.Data;

@Data
public class LogEventData extends LogData {
  private Long userId;
  private Long mainId;
  private Boolean isSystem = false;
  private LogAction action;
  private String objectName;
  private Object preValue;
  private Object value;
}
