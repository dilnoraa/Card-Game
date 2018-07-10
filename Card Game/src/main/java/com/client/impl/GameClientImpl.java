package com.client.impl;

import com.client.api.GameClient;
import com.common.Card;
import com.common.PlayerAction;
import com.game.protocol.FinishRoundRequest;
import com.game.protocol.PlayerActionRequest;
import com.game.protocol.StartRoundRequest;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

public class GameClientImpl implements GameClient {

    static JFrame mainFrame;
    private static JLabel counterLabel = null;
    private static JLabel labelFinishRound = new JLabel();
    private static JLabel labelWinning = new JLabel();
    private static JLabel labelRound;
    private static JLabel labelBaseCard;
    static String action = "";
    static JRadioButton rbHigh, rbLow, rbEqual;
    static ButtonGroup rbGroup;
    static Socket soket;

    private static void createClientUi() {

        mainFrame = new JFrame(" CLIENT WINDOW ");
        mainFrame.getContentPane().setBackground(Color.decode("#CEE3F6"));
        labelRound = new JLabel();
        counterLabel = new JLabel();
        counterLabel.setBounds(200, 60, 20, 20);
        labelBaseCard = new JLabel();
        labelBaseCard.setBounds(110, 10, 160, 20);
        counterLabel.setFont(counterLabel.getFont().deriveFont(20f));
        labelRound.setBounds(10, 10, 100, 20);
        JLabel warningLabel = new JLabel();
        warningLabel.setText(" You have 10 seconds to make choice!");
        warningLabel.setForeground(Color.decode("#610B21"));
        warningLabel.setBounds(10, 30, 250, 20);
        labelFinishRound.setBounds(10, 140, 250, 20);
        labelWinning.setBounds(10, 160, 200, 20);
        rbHigh = new JRadioButton("Higher");
        rbHigh.setBounds(20, 60, 90, 30);
        rbHigh.setBackground(Color.decode("#CEE3F6"));
        rbLow = new JRadioButton("Lower");
        rbLow.setBounds(20, 80, 90, 30);
        rbLow.setBackground(Color.decode("#CEE3F6"));
        rbEqual = new JRadioButton("Equal");
        rbEqual.setBounds(20, 100, 90, 30);
        rbEqual.setBackground(Color.decode("#CEE3F6"));
        rbGroup = new ButtonGroup();
        rbGroup.add(rbHigh);
        rbGroup.add(rbLow);
        rbGroup.add(rbEqual);
        mainFrame.add(rbHigh);
        mainFrame.add(rbLow);
        mainFrame.add(rbEqual);
        mainFrame.add(labelBaseCard);
        mainFrame.add(labelFinishRound);
        mainFrame.add(labelWinning);
        mainFrame.add(counterLabel);
        mainFrame.add(warningLabel);
        mainFrame.setLocation(400, 200);
        mainFrame.setSize(350, 300);
        mainFrame.add(labelRound);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void startRound(StartRoundRequest start_obj) {
        System.out.println("What is your choice? H, L or E");
        try {
            action = getInput(start_obj.getRoundId(), start_obj.getBaseCard());
        } catch (Exception ex) {
            Logger.getLogger(GameClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        PlayerActionRequest player_action = null;

        switch (action) {
            case "Higher":
                player_action = new PlayerActionRequest(PlayerAction.HIGHER);
                break;
            case "Lower":
                player_action = new PlayerActionRequest(PlayerAction.LOWER);
                break;
            case "Equal":
                player_action = new PlayerActionRequest(PlayerAction.EQUALS);
                break;
            case "":
                System.out.println("No action");
                player_action = new PlayerActionRequest(PlayerAction.NOACTION);
                break;
        }

        System.out.println("Player choice is: " + player_action.toString());

        try {
            OutputStream os = soket.getOutputStream();
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(os);
            oos.writeObject(player_action);
        } catch (IOException ex) {
            Logger.getLogger(GameClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void finishRound(FinishRoundRequest finish_round) {

        System.out.println("Display FinishRoundRequest: " + finish_round.toString());

        if (finish_round.isWin()) {
            labelWinning.setText("You win!!");
            System.out.println("You win!!");
        } else {
            labelWinning.setText("You lost!!");
            System.out.println("You lost!!");
        }
        labelFinishRound.setText(finish_round.toString());
    }

    public static String getInput(long RoundId, Card base) throws Exception {
        labelFinishRound.setText("");
        labelWinning.setText("");
        labelBaseCard.setText("Base Card: " + base.toString() + ", Rank:" + base.getIntValue());

        labelRound.setText(" ROUND: " + RoundId);
        System.out.println("You have 10 seconds to make choice: ");
        String selection = "";
        int count = 0;
        while (true) {

            Thread.sleep(1000);
            count++;
            System.out.println(count);
            counterLabel.setText(count + "");
            if (count == 10) {
                break;
            }

            if (rbHigh.isSelected()) {
                selection = rbHigh.getText();
                break;
            }
            if (rbLow.isSelected()) {
                selection = rbLow.getText();
                break;
            }
            if (rbEqual.isSelected()) {
                selection = rbEqual.getText();
                break;
            }
        }
        counterLabel.setText("");
        labelFinishRound.setText("");
        labelWinning.setText("");
        rbGroup.clearSelection();
        return selection;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, Exception {

        System.out.println("CLIENT");
        soket = new Socket("127.0.0.1", 5000);
        createClientUi();
        while (true) {

            System.out.println("");
            System.out.println("");

            InputStream inputS = soket.getInputStream();
            ObjectInputStream objectS = new ObjectInputStream(inputS);
            StartRoundRequest startRound = (StartRoundRequest) objectS.readObject();

            System.out.println("ROUND : " + startRound.getRoundId());
            System.out.println("Base Card: " + startRound.getBaseCard());

            GameClientImpl gameClient = new GameClientImpl();
            gameClient.startRound(startRound);

            InputStream input_stream = soket.getInputStream();
            ObjectInputStream objectStreamF = new ObjectInputStream(input_stream);
            FinishRoundRequest finish = (FinishRoundRequest) objectStreamF.readObject();

            gameClient.finishRound(finish);

            JFrame frameDialog = new JFrame();
            String message = "Do you want continue to next round for Client " + startRound.getclientNo() + " ?";
            int answer = JOptionPane.showConfirmDialog(frameDialog, message);
            if (answer == JOptionPane.YES_OPTION) {
                 System.out.println("Next round is coming...");
            } else if (answer == JOptionPane.NO_OPTION) {            
                soket.close();
                mainFrame.dispose();
                System.exit(0);
                break;
            }
        }
    }
}
