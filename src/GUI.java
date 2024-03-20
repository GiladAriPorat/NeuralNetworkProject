import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class GUI {

    private JPanel panel;
    private JPanel buttonP;
    private JPanel resultP;
    private JLabel L0;
    private JLabel L1;
    private JButton clearButton;
    private JButton randomNumButton;
    private JLabel answerLabel;
    private JLabel L2;
    private JLabel L3;
    private JLabel L4;
    private JLabel L5;
    private JLabel L6;
    private JLabel L7;
    private JLabel L8;
    private JLabel L9;

    static network net = new network(new int[] {784, 256, 10});
    boolean[][] number = new boolean[28][28];
    BigDecimal[] temp = new BigDecimal[784];

    ArrayList<JButton> buttons = new ArrayList<>();
    int change = 0;

    boolean pressedDown = false;

    GUI(){

        Arrays.fill(temp, new BigDecimal("0"));

        buttonP.setLayout(null);

        for(int i = 0; i < 28; i++){

            for(int j = 0; j < 28; j++){

                JButton button = new JButton();
                button.setBounds(j * 15, i * 15, 15, 15);
                buttonP.add(button);

                int finalI = i;
                int finalJ = j;
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mouseClicked(e);

                        pressedDown = true;
                        change = 0;

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseClicked(e);
                        pressedDown = false;
                        change = 0;

                        double[] arr = new double[784];

                        net.setLayer(0, temp);
                        net.propagate();




                        L1.setText("1: confidence --> " + net.layers.get(2).getNodes().get(1).getValue());
                        L0.setText("0: confidence --> " + net.layers.get(2).getNodes().get(0).getValue());
                        L2.setText("2: confidence --> " + net.layers.get(2).getNodes().get(2).getValue());
                        L3.setText("3: confidence --> " + net.layers.get(2).getNodes().get(3).getValue());
                        L4.setText("4: confidence --> " + net.layers.get(2).getNodes().get(4).getValue());
                        L5.setText("5: confidence --> " + net.layers.get(2).getNodes().get(5).getValue());
                        L6.setText("6: confidence --> " + net.layers.get(2).getNodes().get(6).getValue());
                        L7.setText("7: confidence --> " + net.layers.get(2).getNodes().get(7).getValue());
                        L8.setText("8: confidence --> " + net.layers.get(2).getNodes().get(8).getValue());
                        L9.setText("9: confidence --> " + net.layers.get(2).getNodes().get(9).getValue());

                        double temp1 = 0;

                        double Max = net.layers.get(2).getNodes().get(0).getValue().doubleValue();
                        int bestGuess = 0;


                        for(int i = 0; i < 10; i++){

                            temp1 += net.layers.get(2).getNodes().get(i).getValue().doubleValue();

                            if(Max < net.layers.get(2).getNodes().get(i).getValue().doubleValue()){

                                Max = net.layers.get(2).getNodes().get(i).getValue().doubleValue();
                                bestGuess = i;

                            }

                        }

                        double temp2 = (Max/temp1) * 100;

                        answerLabel.setText("Best Guess: " + bestGuess + " with " + temp2 + "% certainty");

                    }

                    @Override
                    public void mouseEntered(MouseEvent e){



                        if(pressedDown) {

                            if(change == 0){

                                if(number[finalJ][finalI]) change = -1;
                                else change = 1;

                            }

                            else if((change == 1 && !number[finalI][finalJ]) || (change == -1 && number[finalI][finalJ])){

                                number[finalI][finalJ] = !number[finalI][finalJ];

                                if (number[finalI][finalJ]) {
                                    temp[finalI * 28 + finalJ % 28] = new BigDecimal("1");

                                    if(finalI > 0){

                                        int Temp = finalI - 1;

                                        temp[Temp * 28 + finalJ % 28] = new BigDecimal("1");
                                        buttons.get(Temp * 28 + finalJ % 28).setBackground(Color.BLACK);

                                        if(finalJ > 0){
                                            buttons.get(Temp * 28 + (finalJ - 1) % 28).setBackground(Color.BLACK);
                                            temp[Temp * 28 + (finalJ - 1) % 28] = new BigDecimal("1");
                                        }
                                        if(finalJ < 27){
                                            buttons.get(Temp * 28 + (finalJ + 1) % 28).setBackground(Color.BLACK);
                                            temp[Temp * 28 + (finalJ+1) % 28] = new BigDecimal("1");
                                        }


                                    }
                                    if(finalI < 27){

                                        int Temp = finalI + 1;

                                        temp[Temp * 28 + finalJ % 28] = new BigDecimal("1");
                                        buttons.get(Temp * 28 + finalJ % 28).setBackground(Color.BLACK);

                                        if(finalJ > 0){
                                            buttons.get(Temp * 28 + (finalJ - 1) % 28).setBackground(Color.BLACK);
                                            temp[Temp * 28 + (finalJ - 1) % 28] = new BigDecimal("1");
                                        }
                                        if(finalJ < 27){
                                            buttons.get(Temp * 28 + (finalJ + 1) % 28).setBackground(Color.BLACK);
                                            temp[Temp * 28 + (finalJ+1) % 28] = new BigDecimal("1");
                                        }

                                    }

                                    if(finalJ < 27){

                                        temp[finalI * 28 + (1 + finalJ) % 28] = new BigDecimal("1");
                                        buttons.get(finalI * 28 + (1 + finalJ) % 28).setBackground(Color.BLACK);

                                    }
                                    if(finalJ > 0){


                                        temp[finalI * 28 + (finalJ - 1) % 28] = new BigDecimal("1");
                                        buttons.get(finalI * 28 + (finalJ - 1) % 28).setBackground(Color.BLACK);

                                    }

                                    button.setBackground(Color.BLACK);
                                } else {
                                    temp[finalI * 28 + finalJ % 28] = new BigDecimal("0");
                                    button.setBackground(Color.WHITE);

                                    if(finalI > 0){

                                        int Temp = finalI - 1;

                                        temp[Temp * 28 + finalJ % 28] = new BigDecimal("0");
                                        buttons.get(Temp * 28 + finalJ % 28).setBackground(Color.WHITE);

                                        if(finalJ > 0){
                                            buttons.get(Temp * 28 + (finalJ - 1) % 28).setBackground(Color.WHITE);
                                            temp[Temp * 28 + (finalJ - 1) % 28] = new BigDecimal("0");
                                        }
                                        if(finalJ < 27){
                                            buttons.get(Temp * 28 + (finalJ + 1) % 28).setBackground(Color.WHITE);
                                            temp[Temp * 28 + (finalJ+1) % 28] = new BigDecimal("0");
                                        }


                                    }
                                    if(finalI < 27){

                                        int Temp = finalI + 1;

                                        temp[Temp * 28 + finalJ % 28] = new BigDecimal("0");
                                        buttons.get(Temp * 28 + finalJ % 28).setBackground(Color.WHITE);

                                        if(finalJ > 0){
                                            buttons.get(Temp * 28 + (finalJ - 1) % 28).setBackground(Color.WHITE);
                                            temp[Temp * 28 + (finalJ - 1) % 28] = new BigDecimal("0");
                                        }
                                        if(finalJ < 27){
                                            buttons.get(Temp * 28 + (finalJ + 1) % 28).setBackground(Color.WHITE);
                                            temp[Temp * 28 + (finalJ+1) % 28] = new BigDecimal("0");
                                        }

                                    }

                                    if(finalJ < 27){

                                        temp[finalI * 28 + (1 + finalJ) % 28] = new BigDecimal("0");
                                        buttons.get(finalI * 28 + (1 + finalJ) % 28).setBackground(Color.WHITE);

                                    }
                                    if(finalJ > 0){


                                        temp[finalI * 28 + (finalJ - 1) % 28] = new BigDecimal("0");
                                        buttons.get(finalI * 28 + (finalJ - 1) % 28).setBackground(Color.WHITE);

                                    }

                                }

                            }

                        }


                    }

                });

                buttons.add(button);
                button.setBackground(Color.WHITE);

            }

        }

        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                Arrays.fill(temp, new BigDecimal("0"));
                for(int i = 0; i < 28; i++) Arrays.fill(number[i], false);
                for(int i = 0; i < buttons.size(); i++) buttons.get(i).setBackground(Color.WHITE);

            }
        });
        randomNumButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int random = (int) (Math.random() * (globalIndex - 1));

                for(int i = 0; i < 784; i++){

                    if(DataBase[random][i / 28][i % 28].doubleValue() > 0.5){
                        buttons.get(i).setBackground(Color.BLACK);
                        number[i / 28][i % 28] = true;
                    }
                    else{
                        buttons.get(i).setBackground(Color.WHITE);
                        number[i / 28][i % 28] = false;
                    }
                    temp[i] = DataBase[random][i / 28][i % 28];

                }

                net.setLayer(0, temp);
                net.propagate();

                L1.setText("1: confidence --> " + net.layers.get(2).getNodes().get(1).getValue());
                L0.setText("0: confidence --> " + net.layers.get(2).getNodes().get(0).getValue());
                L2.setText("2: confidence --> " + net.layers.get(2).getNodes().get(2).getValue());
                L3.setText("3: confidence --> " + net.layers.get(2).getNodes().get(3).getValue());
                L4.setText("4: confidence --> " + net.layers.get(2).getNodes().get(4).getValue());
                L5.setText("5: confidence --> " + net.layers.get(2).getNodes().get(5).getValue());
                L6.setText("6: confidence --> " + net.layers.get(2).getNodes().get(6).getValue());
                L7.setText("7: confidence --> " + net.layers.get(2).getNodes().get(7).getValue());
                L8.setText("8: confidence --> " + net.layers.get(2).getNodes().get(8).getValue());
                L9.setText("9: confidence --> " + net.layers.get(2).getNodes().get(9).getValue());

                answerLabel.setText(Labels[random]);

            }
        });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("grade list");
        frame.setContentPane(new GUI().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        readData();

        net = new network(new int[] {784, 256, 10});
        double[][] labels = new double[globalIndex][10];
        double[][] data = new double[globalIndex][784];
        DataBase = new BigDecimal[globalIndex][28][28];

        for(int i = 0; i < globalIndex; i++){

            Arrays.fill(labels[i], 0);
            labels[i][Integer.parseInt(Labels[i])] = 1;

            for(int j = 0; j < 784; j++){

                data[i][j] = dataPoints[i][j];
                DataBase[i][j / 28][j % 28] = new BigDecimal(dataPoints[i][j]);

            }

        }

        //net.Train(DataBase, 15, 500000, new BigDecimal(0.01), Labels);
        net.LoadPrevParameters();
        System.out.println("READY");

    }

    static BigDecimal[][][] DataBase;

    static int globalIndex = 0;


    static String[] Labels = new String[60000];
    static double[][] dataPoints = new double[60000][784];

    static void readData(){

        try {

            File myObj = new File("dataSet.txt");
            Scanner myReader = new Scanner(myObj);

            globalIndex = 0;

            while(myReader.hasNext()){

                Labels[globalIndex] = myReader.nextLine();

                String number = "";

                for(int i = 0; i < 28; i++){

                    number = number.concat(myReader.nextLine() + " ");

                }

                String[] numbers = number.split(" ");

                for(int i = 0; i < 784; i++){

                    dataPoints[globalIndex][i] = Double.valueOf(numbers[i]);

                }

                globalIndex++;


            }


            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }



    }

}
