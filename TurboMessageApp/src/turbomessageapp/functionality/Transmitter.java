/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.functionality;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import turbomessageapp.logic.Message;
import turbomessageapp.logic.MessageListener;
import turbomessageapp.logic.User;

/**
 * Clase que se encarga del envio y recibo de mensajes.
 * @author franciscojavierlf
 */
public final class Transmitter extends Thread {
  
  private final User user;
  private ArrayList<MessageListener> listeners = new ArrayList<>();
  
  /**
   * Crea un transmisor para un usuario.
   * @param user 
   */
  public Transmitter(User user) {
    this.user = user;
  }
  
  /**
   * Agrega un listener para cuando el usuario recibe mensajes.
   * @param listener 
   */
  public void addListener(MessageListener listener) {
    listeners.add(listener);
  }
  
  /**
   * Envia un mensaje al destinatario correcto.
   * @param message
   * @throws JMSException 
   */
  public boolean sendMessage(Message message) {
    try {
      // Comienza la conexion
      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
      factory.setTrustAllPackages(true);
      Connection conn = factory.createConnection();
      conn.start();

      // Crea una sesion para conectarse con el usuario
      Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination destination = session.createQueue(message.receiver.id);
      MessageProducer producer = session.createProducer(destination);
      ObjectMessage object = session.createObjectMessage();

      // Manda el mensaje
      System.out.println("Enviando mensaje...");
      object.setObject(message);
      producer.send(object);
      System.out.println("Enviado.");

      // Cierra todo
      producer.close();
      session.close();
      conn.close();
      return true;
    } catch(JMSException ex) {
      System.out.println(ex);
    }
    return false;
  }
  
  /**
   * Comienza a escuchar para nuevos mensajes.
   */
  private void listenToMessages() throws JMSException {
    // Comienza la conexion
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
    factory.setTrustAllPackages(true);
    Connection conn = factory.createConnection();
    conn.start();

    // Crea una sesion para conectarse a la cola del usuario
    Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Destination destination = session.createQueue(user.id);
    MessageConsumer consumer = session.createConsumer(destination);
    
    // Recibe mensajes
    ObjectMessage object;
    Message message;
    while (true) {
      // Recibe el primer mensaje en la cola
      object = (ObjectMessage) consumer.receive();
      message = (Message) object.getObject();
      
      // Avisa a los listeners
      switch (message.type) {
        case REQUEST:
          for (MessageListener l : listeners)
            l.onRequestReceived(message);
          break;
        case REQUEST_ACCEPTED:
          for (MessageListener l : listeners)
            l.onRequestAccepted(message);
          break;
        case REQUEST_DENIED:
          for (MessageListener l : listeners)
            l.onRequestDenied(message);
          break;
        case TEXT:
          for (MessageListener l : listeners)
            l.onTextReceived(message);
          break;
        case STATE_UPDATE:
          for (MessageListener l : listeners)
            l.onStateUpdated(message);
          break;
      }
      
      object.setJMSExpiration(0l);
    }
  }
  
  @Override
  public void run() {
    try {
      listenToMessages();
    } catch (JMSException ex) {
      Logger.getLogger(Transmitter.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
