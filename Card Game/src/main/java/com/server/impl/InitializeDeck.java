/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.impl;

import com.common.Card;
import java.util.ArrayList;
 
/**
 *
 * @author pc
 */
public class InitializeDeck {
     public static final int  SIZE=52;
     public  ArrayList<Card> newDeck = new ArrayList<Card>();
     int count=51;
     public InitializeDeck() {
        
     for(int i=0; i<SIZE; i++) {    
        newDeck.add(Card.values()[i]);
     }
     
     int index1;
     
     for(int i=0; i<SIZE; i++) {
     
      index1 = (int) (Math.random() *(SIZE-1));      
      Card temp=newDeck.get(i);
      newDeck.set(i, newDeck.get(index1));
      newDeck.set(index1, temp);
      
     }
     
    
 
    
}
      public Card getCard() {
          Card c= newDeck.get(count);
          newDeck.remove(count);
          count--;
         return c;
     }
      
      
     
}
