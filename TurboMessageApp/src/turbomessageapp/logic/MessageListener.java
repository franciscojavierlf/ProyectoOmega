/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.logic;

/**
 * Interface para escuchar cuando llegan mensajes.
 * @author franciscojavierlf
 */
public interface MessageListener {
  
  /**
   * Cuando un mensaje de texto es recibido.
   */
  public void onTextReceived(Message message);
  
  /**
   * Cuando se recibe una solicitud.
   * @param message 
   */
  public void onRequestReceived(Message message);
  
  /**
   * Cuando una solicitud es aceptada.
   * @param message 
   */
  public void onRequestAccepted(Message message);
  
  /**
   * Cuando una solicitud es rechazada.
   * @param message 
   */
  public void onRequestDenied(Message message);
  
  /**
   * Cuando el estado de un mensaje cambia.
   * @param message 
   */
  public void onStateUpdated(Message message);
}