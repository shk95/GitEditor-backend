package com.shk95.giteditor.common.service.mail;

public interface Mailer {

  /**
   * Send a message.http
   *
   * @param message the message.http instance
   */
  void send(Message message);
}
