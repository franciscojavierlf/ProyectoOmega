/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.functionality;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import turbomessageapp.logic.Message;
import turbomessageapp.logic.MessageListener;
import turbomessageapp.logic.MessageRequest;
import turbomessageapp.logic.User;

/**
 * Administrador de mensajes. Trambien controla la logica de envio y recibo.
 * @author franciscojavierlf
 */
public final class Manager {
  
  public final Bloc bloc;
  public final Transmitter transmitter;
  
  public Manager(User user) {
    bloc = new Bloc();
    transmitter = new Transmitter(user);
    
    // Agrega un listener para reaccionar a nuevos mensajes en la base de datos
    transmitter.addListener(new ManagerMessagesListener(this));
    // Y comienza a escuchar
    transmitter.start();
    // Y se conecta a la bd
    bloc.connect();
  }
  
  public void addMessageListener(MessageListener listener) {
    transmitter.addListener(listener);
  }
  
  /**
   * Manda una solicitud a otro usuario.
   * @param senderId
   * @param receiverId
   * @param body
   * @return 
   */
  public MessageRequest sendRequest(User sender, String receiverId) {
    // No envia solicitud si ya existe para cualquier lado
    if (bloc.requestExists(sender.id, receiverId) || bloc.requestExists(receiverId, sender.id))
      return null;
    // Crea un request y lo agrega a la bd
    Message request = bloc.addRequest(sender, receiverId);
    // Y se transmite el mensaje para el usuario
    transmitter.sendMessage(request);
    return new MessageRequest(request.sender, request.receiver, MessageRequest.State.PENDING);
  }
  
  /**
   * Checa si dos usuarios pueden comunicarse entre si (si estan en el directorio).
   * @param userId1
   * @param userId2
   * @return 
   */
  public boolean canCommunicate(String userId1, String userId2) {
    MessageRequest request = bloc.getRequest(userId1, userId2);
    if (request != null && request.state == MessageRequest.State.ACCEPTED)
      return true;
    request = bloc.getRequest(userId2, userId1);
    if (request != null && request.state == MessageRequest.State.ACCEPTED)
       return true;
    return false;
  }
  
  
  /**
   * Acepta una solicitud.
   * @param sender
   * @param receiverId
   * @return 
   */
  public MessageRequest acceptRequest(String senderId, String receiverId) {
    if (bloc.requestExists(senderId, receiverId)) {
      Message message = bloc.addAcceptedRequest(senderId, receiverId);
      transmitter.sendMessage(message);
      return new MessageRequest(message.sender, message.receiver, MessageRequest.State.ACCEPTED);
    }
    return null;
  }
  
  /**
   * Rechaza una solicitud.
   * @param sender
   * @param receiverId
   * @return 
   */
  public MessageRequest denyRequest(String senderId, String receiverId) {
    if (bloc.requestExists(senderId, receiverId)) {
      Message message = bloc.addDeniedRequest(senderId, receiverId);
      transmitter.sendMessage(message);
      return new MessageRequest(message.sender, message.receiver, MessageRequest.State.DENIED);
    }
    return null;
  }
  
  /**
   * Manda un mensaje de texto.
   * @param sender
   * @param receiverId
   * @param body
   * @return 
   */
  public Message sendTextMessage(User sender, String receiverId, String body) {
    
    if (!canCommunicate(sender.id, receiverId)) {
      System.out.println(sender.id + " y " + receiverId + " no se pueden comunicar!");
      return null;
    }
    Message message = bloc.addTextMessage(sender, receiverId, body);
    transmitter.sendMessage(message);
    return message;
  }
  
   /**
    * Actualiza el estado de un mensaje.
    * @param message
    * @param newState
    * @return 
    */
  public Message updateMessageState(Message message, Message.State newState) {
    // Lo actualiza en la base de datos y luego avisa al usuario
    Message update = bloc.updateMessageState(message, newState);
    transmitter.sendMessage(update);
    return update;
  }
  
  /**
   * Clase para movimientos en la base de datos al recibir mensajes.
   */
  private class ManagerMessagesListener implements MessageListener {
    
    private final Manager manager;
    
    public ManagerMessagesListener(Manager manager) {
      this.manager = manager;
    }

    @Override
    public void onTextReceived(Message message) {
      manager.updateMessageState(message, Message.State.DELIVERED);
    }

    @Override
    public void onRequestReceived(Message message) {
    }

    @Override
    public void onRequestAccepted(Message message) {
    }

    @Override
    public void onRequestDenied(Message message) {
    }

    @Override
    public void onStateUpdated(Message message) {
    }
    
  }
}
