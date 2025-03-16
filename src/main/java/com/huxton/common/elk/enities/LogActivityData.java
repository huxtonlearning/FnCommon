package com.huxton.common.elk.enities;

import com.huxton.common.elk.enities.base.LogData;
import com.huxton.common.elk.enums.LogAction;
import lombok.Data;

import java.util.Map;

@Data
public class LogActivityData extends LogData {
  private Long userId;
  private Long mainId;
  private Boolean isSystem = false;
  private LogAction action;
  private Object relatedObject;
  //  private String ipAddress;
  //  private String device;
  //  private String location;
  private Object performedBy;
  private Map<String, Object> extraFields;
}
