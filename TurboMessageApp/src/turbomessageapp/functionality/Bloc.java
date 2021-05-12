/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.functionality;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Statement;
import org.apache.camel.util.Time;
import turbomessageapp.logic.Message;
import turbomessageapp.logic.MessageRequest;
import turbomessageapp.logic.User;

/**
 * Clase que se encarga del registro de los mensajes en la base de datos.
 * @author franciscojavierlf
 */
public class Bloc {
  
  private static int count = 0; // Temporal
  
  private static String URL = "jdbc:derby://localhost:1527/TurboMessageApp;create=true;user=frank;password=admin";
  private static Connection connection = null;
  private static Statement statement = null;
  
  /**
   * Se conecta a la base de datos.
   */
  public void connect() {
    // Ya estamos conectados
    if (connection != null)
      return;
    try {
      Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
      connection = DriverManager.getConnection(URL, "frank", "admin"); 
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Se desconecta de la base de datos.
   */
  public void close() {
    try {
      if (statement != null)
        statement.close();
      if (connection != null)
        connection.close();
    } catch(SQLException ex) {
      System.out.println(ex);
    }
  }
  
  /**
   * Crea un nuevo usuario en la base de datos.
   * @param id
   * @param name
   * @return 
   */
  public User createUser(String id, String name) {
    try {
      statement = connection.createStatement();
      statement.execute("INSERT INTO USERS (ID, NAME) VALUES ('" + id + "', '" + name + "')");
      statement.close();
      return new User(id, name);
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }
  
  /**
   * Regresa el usuario.
   * @param userId
   * @return 
   */
  public User getUser(String userId) {
    try {
      statement = connection.createStatement();
      ResultSet result = statement.executeQuery("SELECT * FROM USERS WHERE ID='" + userId + "'");
      if (result.next()) {
        return new User(result.getString(1), result.getString(2));
      }
    } catch(SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }
  
  /**
   * Checa si el id de un usuario ya esta en uso.
   * @param userId
   * @return 
   */
  public boolean userExists(String userId) {
    return getUser(userId) != null;
  }
  
  /**
   * Regresa al usuario e indica si se quiere el directorio o no.
   * @param userId
   */
  public HashMap<String, MessageRequest> getUserDirectory(User user) {
    HashMap<String, MessageRequest> map = new HashMap<>();
    
    try {
      statement = connection.createStatement();
      ResultSet results = statement.executeQuery("SELECT * FROM DIRECTORY WHERE SENDER_ID='" + user.id + "' OR RECEIVER_ID='" + user.id + "'");
      
      // Convierte a directorio
      User contact;
      User sender;
      User receiver;
      MessageRequest.State state;
      while (results.next()) {
        contact = results.getString(1).equals(user.id) ? getUser(results.getString(2)) : getUser(results.getString(1));
        sender = results.getString(1).equals(user.id) ? user : contact;
        receiver = sender.equals(user) ? contact : user;
        state = MessageRequest.State.valueOf(results.getString(3));
        map.put(contact.id, new MessageRequest(sender, receiver, state));
      }
      
      results.close();
      statement.close();
    } catch(SQLException ex) {
      System.out.println(ex);
    }
    return map;
  }
  
  /**
   * Obtiene una solicitud.
   * @param sender
   * @param receiverId
   * @return 
   */
  public MessageRequest getRequest(String senderId, String receiverId) {
    try {
      statement = connection.createStatement();
      ResultSet result = statement.executeQuery("SELECT * FROM DIRECTORY WHERE SENDER_ID='" + senderId + "' AND RECEIVER_ID='" + receiverId + "'");
      if (result.next()) {
        User sender = getUser(senderId);
        User receiver = getUser(receiverId);
        return new MessageRequest(sender, receiver, MessageRequest.State.valueOf(result.getString(3)));
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }
  
  /**
   * Checa si una solicitud existe.
   * @param sender
   * @param receiverId
   * @return 
   */
  public boolean requestExists(String senderId, String receiverId) {
    return getRequest(senderId, receiverId) != null;
  }
  
  /**
   * Regresa una lista de los mensajes de una conversacion.
   * @param user
   * @return 
   */
  public ArrayList<Message> getChatHistory(User user, User contact) {
    
    ArrayList<Message> messages = new ArrayList<>();
    
    try {
      statement = connection.createStatement();
      ResultSet results = statement.executeQuery("SELECT * FROM MESSAGES WHERE (SENDER_ID='" + user.id + "' OR RECEIVER_ID='" + contact.id
                                                + "') AND (SENDER_ID='" + user.id + "' OR RECEIVER_ID='" + contact.id + "')");
            
      // Obtiene los mensajes
      while (results.next())
        messages.add(messageFromQuery(user, contact, results));
      
      // Cierra
      results.close();
      statement.close();
      
    } catch(SQLException ex) {
      System.out.println(ex);
    }
    
    return messages;
  }
  
  /**
   * Consigue todos los mensajes que ha mandado o recibido el usuario.
   * @param user
   * @return 
   */
  public ArrayList<Message> getMessages(User user) {
    ArrayList<Message> messages = new ArrayList<>();
    HashMap<String, User> users = new HashMap<>();
    
    try {
      statement = connection.createStatement();
      ResultSet results = statement.executeQuery("SELECT * FROM MESSAGES WHERE SENDER_ID='" + user.id + "' OR RECEIVER_ID='" + user.id + "'");
      
      String contactId;
      User contact;
      while (results.next()) {
        // Asi no descargamos dos veces a un mismo usuario
        contactId = results.getString(2).equals(user.id) ? results.getString(3) : results.getString(2);
        contact = users.get(contactId) == null ? getUser(contactId) : users.get(contactId);
        messages.add(messageFromQuery(user, contact, results));
      }
      // Cierra
      results.close();
      statement.close();
      
    } catch(SQLException ex) {
      System.out.println(ex);
    }
    
    return messages;
  }
  
  /**
   * Crea un mensaje de un query, seleccionando el cursor actual.
   * @param result
   * @return 
   */
  private Message messageFromQuery(User user, User contact, ResultSet result) throws SQLException {

    int id = result.getInt(1);
    User sender = result.getString(2).equals(user.id) ? user : contact;
    User receiver = sender.equals(user) ? contact : user;
    Message.Type type = Message.Type.valueOf(result.getString(4));
    String body = result.getString(5);
    Message.State state = Message.State.valueOf(result.getString(6));
    
    return new Message(id, sender, receiver, type, body, state);
  }
  
  /**
   * Agrega una solicitud a la base de datos.
   * @param senderId
   * @param receiverId
   * @param body
   * @return 
   */
  public Message addRequest(User sender, String receiverId) {
    
    // Agrega el mensaje
    int id = (int) System.currentTimeMillis();
    User receiver = getUser(receiverId);
    Message res = new Message(id, sender, receiver, Message.Type.REQUEST);
    
    // Luego escribe en directorio
    addToDirectory(sender, receiver, MessageRequest.State.PENDING);
    
    return res;
  }
  
  /**
   * Acepta una solicitud.
   * @param sender
   * @param receiverId
   * @return 
   */
  public Message acceptRequest(String senderId, String receiverId) {
    
    // Agrega el mensaje de aceptado
    int id = (int) System.currentTimeMillis();
    User sender = getUser(senderId);
    User receiver = getUser(receiverId);
    Message res = new Message(id, receiver, sender, Message.Type.REQUEST_ACCEPTED);
    
    // Y luego se actualiza en el directorio
    updateDirectory(sender, receiver, MessageRequest.State.ACCEPTED);
            
    return res;
  }
  
  /**
   * Rechaza una solicitud.
   * @param sender
   * @param receiverId
   * @return 
   */
  public Message denyRequest(String senderId, String receiverId) {
    
    // Agrega el mensaje de no aceptado
    int id = (int) System.currentTimeMillis();
    User sender = getUser(senderId);
    User receiver = getUser(receiverId);
    Message res = new Message(id, receiver, sender, Message.Type.REQUEST_DENIED);
    
    // Y luego se actualiza en el directorio
    updateDirectory(sender, receiver, MessageRequest.State.DENIED);    
    
    return res;
  }
  
  /**
   * Checa si dos usuarios pueden comunicarse entre si (si estan en el directorio).
   * @param userId1
   * @param userId2
   * @return 
   */
  public boolean canCommunicate(String userId1, String userId2) {
    MessageRequest request = getRequest(userId1, userId2);
    if (request != null && request.state == MessageRequest.State.ACCEPTED)
      return true;
    request = getRequest(userId2, userId1);
    if (request != null && request.state == MessageRequest.State.ACCEPTED)
       return true;
    return false;
  }
  
  /**
   * Agrega un dato al directorio.
   * @param sender
   * @param receiver
   * @param state 
   */
  private void addToDirectory(User sender, User receiver, MessageRequest.State state) {
    try {
      statement = connection.createStatement();
      statement.execute("INSERT INTO DIRECTORY (SENDER_ID, RECEIVER_ID, STATE) VALUES ('" + sender.id + "', '" + receiver.id + "', '" + state.toString() + "')");
      statement.close();
    } catch (SQLException ex) {
      System.out.println(ex);
    }
  }
  
  /**
   * Actualiza un dato en el directorio.
   * @param sender
   * @param receiver
   * @param state 
   */
  private void updateDirectory(User sender, User receiver, MessageRequest.State state) {
    try {
      statement = connection.createStatement();
      statement.execute("UPDATE DIRECTORY SET STATE='" + state.toString() + "' WHERE SENDER_ID='" + sender.id + "' AND RECEIVER_ID='" + receiver.id + "'");
      statement.close();
    } catch (SQLException ex) {
      System.out.println(ex.getStackTrace());
    }
  }
  
  /**
   * Agrega un mensaje de texto.
   * @param sender
   * @param receiverId
   * @param body
   * @return 
   */
  public Message addTextMessage(User sender, String receiverId, String body) {
    if (!canCommunicate(sender.id, receiverId))
      return null;
    
    User receiver = getUser(receiverId);
    return addMessage(sender, receiver, Message.Type.TEXT, body);
  }
  
  /**
   * Actualiza el estado de un mensaje.
   * @param message 
   */
  public Message updateMessageState(Message message, Message.State newState) {
    // Tecnicamente solo se puede actualizar el estado (leido, llegado, etc)
    message.state = newState;
    try {
      statement = connection.createStatement();
      statement.execute("UPDATE MESSAGES SET STATE='" + newState.toString() + "' WHERE ID=" + message.id);
      statement.close();
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    
    return new Message(message.id, message.receiver, message.sender, Message.Type.STATE_UPDATE, message.id + "", newState);
  }
  
  /**
   * Funcion generica para agregar nuevos mensajes.
   * @param message 
   */
  private Message addMessage(User sender, User receiver, Message.Type type, String body) {
    
    Message.State state = Message.State.SENT;
    
    try {
      //String query = "INSERT INTO MESSAGES (SENDER_ID, RECEIVER_ID, TYPE, BODY, STATE) VALUES ('"
      //  + sender.id + "', '" + receiver.id + "', '" + type.toString() + "', '" + body + "', '" + state.toString() + "')";
      String query = "INSERT INTO MESSAGES (SENDER_ID, RECEIVER_ID, TYPE, BODY, STATE) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, sender.id);
      statement.setString(2, receiver.id);
      statement.setString(3, type.toString());
      statement.setString(4, body);
      statement.setString(5, state.toString());
      
      statement.execute();
      
      ResultSet result = statement.getGeneratedKeys();
      
      if (result.next()) {
        int id = result.getInt(1);
        statement.close();
        return new Message(id, sender, receiver, type, body, state);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }
}