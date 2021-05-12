/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.logic;

import java.io.Serializable;

/**
 *
 * @author franciscojavierlf
 */
public final class Message implements Serializable {
  
  /**
   * Tipos de mensajes.
   */
  public enum Type {
    REQUEST, REQUEST_ACCEPTED, REQUEST_DENIED, TEXT, STATE_UPDATE
  }
  
  /**
   * Estados de un mensaje.
   */
  public enum State {
    SENT, DELIVERED, READ
  }
  
  public final int id;
  public final User sender;
  public final User receiver;
  public final Type type;
  public final String body;
  public State state;
  
  /**
   * Crea un nuevo mensaje.
   * @param id
   * @param senderId
   * @param receiverId
   * @param type
   * @param body
   * @param state 
   */
  public Message(int id, User sender, User receiver, Type type, String body, State state) {
    this.id = id;
    this.sender = sender;
    this.receiver = receiver;
    this.type = type;
    this.body = body;
    this.state = state;
  }
  
  /**
   * Crea un mensaje que no requiere de cuerpo ni de estado.
   * @param id
   * @param senderId
   * @param receiverId
   * @param type 
   */
  public Message(int id, User sender, User receiver, Type type) {
    this(id, sender, receiver, type, "", State.SENT);
  }  
}
