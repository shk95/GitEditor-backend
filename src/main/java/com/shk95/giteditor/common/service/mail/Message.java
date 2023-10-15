package com.shk95.giteditor.common.service.mail;

public interface Message {

  /**
   * Get the recipient of the message.http
   *
   * @return recipient's email address
   */
  String getTo();

  /**
   * Get the subject of the message.http
   *
   * @return message.http's subject
   */
  String getSubject();

  /**
   * Get the body of the message.http
   *
   * @return the body of the message.http
   */
  String getBody();

  /**
   * Get the from of this message.http
   *
   * @return where this message.http is from
   */
  String getFrom();
}
