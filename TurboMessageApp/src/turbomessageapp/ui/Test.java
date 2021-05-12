/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.ui;

import turbomessageapp.functionality.Bloc;
import turbomessageapp.logic.User;

/**
 *
 * @author franciscojavierlf
 */
public class Test {
  
  public static void main(String[] args) {
    Bloc b = new Bloc();
    b.connect();
    User u1 = b.getUser("frank");
    User u2 = b.getUser("javier");
    Dashboard b1 = new Dashboard(u1);
    Dashboard b2 = new Dashboard(u2);
    b1.setVisible(true);
    b2.setVisible(true);
  }
}
