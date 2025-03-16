package com.huxton.common.elk.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huxton.common.elk.ElkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class LogInterceptor implements HandlerInterceptor {
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final ElkService elkService;

  public LogInterceptor(ElkService elkService) {
    this.elkService = elkService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    long startTime = System.currentTimeMillis();
    request.setAttribute("startTime", startTime);
    return true; // Proceed with the execution chain  }
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//    HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    long startTime = (long) request.getAttribute("startTime");
    long endTime = System.currentTimeMillis();
    long timeTaken = endTime - startTime;
    elkService.whiteLogRequest(request, response, timeTaken);
  }

  //  @Override
//  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//      throws Exception {
//    long startTime = System.currentTimeMillis();
//    request.setAttribute("startTime", startTime);
//    return true; // Proceed with the execution chain
//  }
//
//  @Override
//  public void postHandle(
//      HttpServletRequest request,
//      HttpServletResponse response,
//      Object handler,
//      ModelAndView modelAndView)
//      throws Exception {
//    // Modify the model and view if needed
//  }
//
//  @Override
//  public void afterCompletion(
//      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//      throws Exception {
//    long startTime = (long) request.getAttribute("startTime");
//    long endTime = System.currentTimeMillis();
//    long timeTaken = endTime - startTime;
//    elkService.whiteLogRequest(request, response, timeTaken);
//  }

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
}
