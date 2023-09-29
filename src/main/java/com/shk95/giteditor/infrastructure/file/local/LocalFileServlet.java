package com.shk95.giteditor.infrastructure.file.local;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet("/local-file/*")
@Slf4j
public class LocalFileServlet extends HttpServlet {

  private static final long serialVersionUID = 5275806066971699486L;
  private final String localRootPath;
  private final Environment environment;

  public LocalFileServlet(@Value("${app.file-storage.local-root-folder}") String localRootPath,
                          Environment environment) {
    this.localRootPath = localRootPath;
    this.environment = environment;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (environment.acceptsProfiles("production", "staging")) {
      String activeProfiles = String.join(", ", environment.getActiveProfiles());
      log.warn("Access `{}` in environment `{}`. IP address: `{}`.", request.getPathInfo(), activeProfiles, request.getRemoteAddr());
    }

    String pathInfo = request.getPathInfo();
    if ("/".equals(pathInfo)) {
      response.getWriter().write("/");
      return;
    }

    String filePath = localRootPath + request.getPathInfo();
    File file = new File(filePath);
    if (!file.exists() || file.isDirectory()) {
      response.sendError(404);
      return;
    }

    response.setContentType(request.getServletContext().getMimeType(pathInfo));
    response.setHeader("Cache-Control", "public, max-age=31536000");
    Files.copy(Paths.get(localRootPath, pathInfo), response.getOutputStream());
  }

}
