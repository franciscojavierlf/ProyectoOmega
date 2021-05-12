/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.logic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 * Un usuario de la app.
 * @author franciscojavierlf
 */
public final class User implements Serializable {
  
  public final String id;
  public final String name;
  
  /**
   * Crea un usuario con un directorio vacio.
   * @param id
   * @param name 
   */
  public User(String id, String name) {
    this.id = id;
    this.name = name;
  }
  
  @Override
  public String toString() {
    return id;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass())
      return false;
    final User other = (User) obj;
    if (!Objects.equals(this.id, other.id))
      return false;
    return true;
  }
}
