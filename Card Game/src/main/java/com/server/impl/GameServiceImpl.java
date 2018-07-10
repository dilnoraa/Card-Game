package com.server.impl;

import com.game.protocol.PlayerActionRequest;
import com.game.protocol.PlayerActionResponse;
import com.server.api.GameService;
import com.server.api.SetBaseCardRequest;
import com.server.api.SetBaseCardResponse;
import com.common.Card;
import com.common.PlayerAction;
import com.game.protocol.FinishRoundRequest;
import com.game.protocol.StartRoundRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GameServiceImpl implements GameService {

    ServerSocket server_socket = null;
    Socket client_socket = null;
    ExecutorService executor = null;
    static int clientcount = 0;
    private static JLabel labelAmount;
    static Card baseCard;
    static Card resultCard;

    public static void main(String[] args) throws IOException {

        GameServiceImpl gameService = new GameServiceImpl();
        gameService.startConnection();

    }
    
    @Override
    public boolean playerAction(PlayerActionRequest player_action) {

        boolean winning = false;
        System.out.println("Player's choice :  " + player_action.getPlayerAction());
        PlayerAction trueAction;
        if (baseCard.getIntValue() < resultCard.getIntValue()) {
            trueAction = PlayerAction.HIGHER;
        } else if (baseCard.getIntValue() > resultCard.getIntValue()) {
            trueAction = PlayerAction.LOWER;
        } else {
            trueAction = PlayerAction.EQUALS;
        }
        if (trueAction == player_action.getPlayerAction()) {
            System.out.println("Player won.");
            winning = true;
        } else {
            System.out.println("Player lost.");
            winning = false;
        }

        return winning;
    }
    
     /*  @Override
    public SetBaseCardResponse setBaseCard(SetBaseCardRequest setBaseCardRequest) {

        return new SetBaseCardResponse("");
    }*/

    GameServiceImpl() {

        executor = Executors.newFixedThreadPool(2);
    }

    private static void ServerGui() {
        JFrame frame = new JFrame("SERVER WINDOW");
        frame.getContentPane().setBackground(Color.decode("#A9BCF5"));
        labelAmount = new JLabel();
        labelAmount.setText("Amount of clients is 0");
        labelAmount.setBounds(20, 40, 130, 20);
        frame.add(labelAmount);
        frame.setLocation(750, 200);
        frame.setSize(250, 200);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void startConnection() throws IOException {
        System.out.println("SERVER");
        ServerGui();
        server_socket = new ServerSocket(5000);
        System.out.println("Server Booted");
        while (true) {
            client_socket = server_socket.accept();
            clientcount++;
            labelAmount.setText("Amount of clients is " + clientcount);
            ServerThread runnable = new ServerThread(client_socket, clientcount, this);
            executor.execute(runnable);
        }
    }

    private static class ServerThread implements Runnable {

        GameServiceImpl gameService = null;
        Socket clientSocket = null;
        int id;
      
        ServerThread(Socket client, int count, GameServiceImpl server) throws IOException {

            this.clientSocket = client;
            this.gameService = server;
            this.id = count;        
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("CONNECTION " + id + " established with Client " + client);
        }

        @Override
        public void run() {
            InitializeDeck deck = new InitializeDeck();
            baseCard = deck.getCard();
            boolean win = false;
            int roundId = 0;

            while (true) {
                roundId++;
                if (deck.count == 0) {
                    deck = new InitializeDeck();
                    baseCard = deck.getCard();
                    System.out.println(" Deck got empty so the new deck was initialized.");
                }

                System.out.println("ROUND : " + roundId);
                System.out.println("Base Card : " + baseCard);
                resultCard = deck.getCard();
                System.out.println("Result Card : " + resultCard);

                StartRoundRequest start = new StartRoundRequest(10, System.currentTimeMillis(), roundId, baseCard, clientcount);
                OutputStream output_stream = null;
                ObjectOutputStream object_stream = null;
                try {
                    output_stream = clientSocket.getOutputStream();
                    object_stream = new ObjectOutputStream(output_stream);
                    object_stream.writeObject(start);
                } catch (IOException ex) {
                    Logger.getLogger(GameServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("Server sends StartRoundRequest to Client ");
                PlayerActionRequest player_Act = null;

                try {
                    InputStream input_stream = clientSocket.getInputStream();
                    ObjectInputStream obj_inputstream = new ObjectInputStream(input_stream);
                    player_Act = (PlayerActionRequest) obj_inputstream.readObject();

                    win = gameService.playerAction(player_Act);

                } catch (Exception ex) {
                    //  Logger.getLogger(GameServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }

                FinishRoundRequest finRound = new FinishRoundRequest(roundId, win);
                OutputStream os_finish = null;
                ObjectOutputStream obj_finish = null;
                try {
                    os_finish = clientSocket.getOutputStream();
                    obj_finish = new ObjectOutputStream(os_finish);
                    obj_finish.writeObject(finRound);
                } catch (IOException ex) {
                    Logger.getLogger(GameServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("Result Card: " + resultCard + " will be Base Card for next round.");
                baseCard = resultCard;
                System.out.println("");
                System.out.println("");
            }
        }
    }

}
