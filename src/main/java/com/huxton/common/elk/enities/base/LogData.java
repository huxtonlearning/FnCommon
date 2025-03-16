package com.huxton.common.elk.enities.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huxton.common.elk.enums.LogType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogData {
  protected LogType type;

  @JsonProperty("created_at")
  protected LocalDateTime createdAt;
}
