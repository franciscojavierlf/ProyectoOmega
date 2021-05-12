/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.logic;

/**
 * El estado de una solicitud.
 * @author franciscojavierlf
 */
public final class MessageRequest {
  
  public enum State {
    PENDING, ACCEPTED, DENIED
  }
  
  public final User sender;
  public final User receiver;
  public State state;
  
  public MessageRequest(User sender, User receiver, State state) {
    this.sender = sender;
    this.receiver = receiver;
    this.state = state;
  }
  
  public User getOpposite(User user) {
    return sender.equals(user) ? receiver : sender;
  }
}
