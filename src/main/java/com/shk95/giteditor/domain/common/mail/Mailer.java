package com.shk95.giteditor.domain.common.mail;

public interface Mailer {

  /**
   * Send a message
   *
   * @param message the message instance
   */
  void send(Message message);
}
