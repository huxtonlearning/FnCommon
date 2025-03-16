package com.huxton.common.elk;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huxton.common.exceptions.ExceptionResponse;
import com.huxton.common.elk.enities.*;
import com.huxton.common.elk.enums.LogAction;
import com.huxton.common.elk.enums.LogBrowser;
import com.huxton.common.elk.enums.LogStatus;
import com.huxton.common.elk.enums.LogType;
import com.huxton.common.utils.JsonParser;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class ElkService {

  @Async
  public void whiteLogObject(Long userId, String className, Long objectId, LogAction logAction, Object data) {
    LogObjectData logObjectData = new LogObjectData();
    logObjectData.setUserId(userId);
    logObjectData.setVersion(1);
    logObjectData.setType(LogType.OBJECT);
    logObjectData.setObjectName(className);
    logObjectData.setObjectId(objectId);
    logObjectData.setAction(logAction);
    logObjectData.setData(data);
    logObjectData.setStatus(LogStatus.SUCCESS);
    logObjectData.setCreatedAt(LocalDateTime.now());
    log.info(JsonParser.toJson(logObjectData));
  }

  @Async
  public void whiteLogApi(Long userId, String url, HttpMethod method, Object auth, HttpStatus status, Object body, Object response) {
    LogApiData logApiData = new LogApiData();
    logApiData.setAuth(auth);
    logApiData.setUrl(url);
    logApiData.setBody(body);
    logApiData.setResponse(response);
    logApiData.setStatus(status);
    logApiData.setMethod(method);
    logApiData.setUserId(userId);
    logApiData.setType(LogType.API);
    logApiData.setCreatedAt(LocalDateTime.now());
    log.info(JsonParser.toJson(logApiData));
  }

  @Async
  public void whiteLogException(ExceptionResponse exceptionResponse, HttpServletRequest request) {
    LogExceptionData logExceptionData = new LogExceptionData();
    logExceptionData.setDescription(exceptionResponse.getDescription());
    logExceptionData.setTimestamp(exceptionResponse.getTimestamp());
    logExceptionData.setParams(request.getParameterMap());
//        logExceptionData.setBody(getBody(request, response));
    logExceptionData.setHeaders(getHeaderMap(request));
    logExceptionData.setPath(exceptionResponse.getPath());
    logExceptionData.setMessage(exceptionResponse.getMessage());
    logExceptionData.setMessageCode(exceptionResponse.getMessageCode());
    logExceptionData.setStatus(exceptionResponse.getStatus());
    logExceptionData.setType(LogType.EXCEPTION);
    logExceptionData.setCreatedAt(LocalDateTime.now());
    log.info(JsonParser.toJson(logExceptionData));
  }

  @Async
  public void whiteLogRequest(Long userId, String url, HttpMethod method, Object body, HttpStatus status, Object response) {
    LogRequestData logRequestData = new LogRequestData();
    logRequestData.setUserId(userId);
    logRequestData.setType(LogType.REQUEST);
    logRequestData.setCreatedAt(LocalDateTime.now());
    logRequestData.setPath(url);
    logRequestData.setMethod(String.valueOf(method));
    logRequestData.setBody(body);
    logRequestData.setResponse(response);
    logRequestData.setStatus(status);

    log.info(JsonParser.toJson(logRequestData));

  }

  @Async
  public void whiteLogRequest(HttpServletRequest request, HttpServletResponse response, Long timeTaken) throws IOException {
    if (request.getMethod().equals("OPTIONS")) return;
    LogRequestData logRequestData = new LogRequestData();
    logRequestData.setStatus(HttpStatus.valueOf(response.getStatus()));
    logRequestData.setIntStatus(response.getStatus());
    logRequestData.setPath(request.getServletPath());
    logRequestData.setMethod(request.getMethod());
    logRequestData.setParams(request.getParameterMap());
    logRequestData.setResponse(response.getTrailerFields());
    Map<String, String> headers = getHeaderMap(request);
    logRequestData.setHeader(headers);
    logRequestData.setType(LogType.REQUEST);
    logRequestData.setCreatedAt(LocalDateTime.now());
//    if (headers.get("x-om-user-id") != null) logRequestData.setUserId(Long.valueOf(headers.get("x-om-user-id")));
    logRequestData.setBody(getBody(request));
    logRequestData.setTimeTaken(timeTaken);
    log.info(JsonParser.toJson(logRequestData));
  }

  @Async
  public void whiteLogEventSystem(Long userId, Long enterpriseId, Class<?> objectName, @NotNull LogAction action, Object preValue, Object value, Boolean isSystem) {
    LogEventData data = new LogEventData();
    data.setAction(action);
    data.setMainId(enterpriseId);
    data.setValue(value);
    data.setIsSystem(isSystem);
    data.setPreValue(preValue);
    data.setUserId(userId);
    data.setObjectName(objectName.getSimpleName().toUpperCase());
    data.setType(LogType.EVENT);
    data.setCreatedAt(LocalDateTime.now());
    log.info(JsonParser.toJson(data));
  }

  @Async
  public void whiteLogEvent(Long userId, Long enterpriseId, Class<?> objectName, @NotNull LogAction action, Object preValue, Object value) {
    LogEventData data = new LogEventData();
    data.setAction(action);
    data.setIsSystem(false);
    data.setMainId(enterpriseId);
    data.setValue(value);
    data.setPreValue(preValue);
    data.setUserId(userId);
    data.setObjectName(objectName.getSimpleName().toUpperCase());
    data.setType(LogType.EVENT);
    data.setCreatedAt(LocalDateTime.now());
    log.info(JsonParser.toJson(data));
  }

  private Map<String, String> getHeaderMap(HttpServletRequest request) {
    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);
      headers.put(headerName, headerValue);
    }
    return headers;
  }

  private JsonNode getBody(HttpServletRequest request) throws IOException {
    // TODO: get body in request
    return null;
//      return objectMapper.readTree(requestBody);
  }


}
