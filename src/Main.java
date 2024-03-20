import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.math.RoundingMode;


public class Main {

    public static void main(String[] args) {


    }

}

class network{
    ArrayList<layer> layers = new ArrayList<>();


    void LoadPrevParameters(){

        try {
            File myObj = new File("CalculatedWeights.txt");
            Scanner myReader = new Scanner(myObj);

            for(int i = 0; i < 256; i++){

                String DAta = myReader.nextLine();
                this.layers.get(1).getNodes().get(i).Bias = new BigDecimal(Double.parseDouble(DAta));


            }

            for(int i = 0; i < 10; i++){

                String DAta = myReader.nextLine();
                this.layers.get(2).getNodes().get(i).Bias = new BigDecimal(Double.parseDouble(DAta));

            }


            for(int i = 0; i < 256; i++){

                for(int j = 0; j < 784; j++){

                    String DAta = myReader.nextLine();
                    this.layers.get(0).getNodes().get(j).weights[i] = new BigDecimal(Double.parseDouble(DAta));

                }



            }


            for(int i = 0; i < 10; i++){

                for(int j = 0; j < 256; j++){

                    String DAta = myReader.nextLine();
                    this.layers.get(1).getNodes().get(j).weights[i] = new BigDecimal(Double.parseDouble(DAta));

                }



            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    void Train(BigDecimal[][][] data, int batchSize, int EpochLim, BigDecimal learningRate, String[] labels){

        int Epoch = 0;
        double Error = Double.MAX_VALUE;

        while(Epoch <= EpochLim){

            Epoch++;
            if(Epoch % 10 == 0) System.out.println("Epoch: " + Epoch);

            for(int i = 0; i < batchSize; i++){

                int rand = (int) (Math.random() * (data.length - 1));
                //rand = 1;

                for(int j = 0; j < 784; j++) {
                    this.layers.get(0).getNodes().get(j).setValue(data[rand][j/28][j%28]);
                }
                BigDecimal[] expectedValues = new BigDecimal[10];
                Arrays.fill(expectedValues, BigDecimal.ZERO);
                expectedValues[Integer.parseInt(labels[rand])] = new BigDecimal(1);
                this.propagate();
                this.calcOutputLayerError(expectedValues);
                this.backProp(expectedValues, learningRate);
            }
            System.out.println(" EPOCH:" + Epoch);
        }

    }

    void PrintNetwork(){

        int counter = 1;
        for(layer l : layers){

            System.out.println("layer " + counter);
            l.print();
            counter++;
        }

    }

    network(int[] Layers){

        for(int i = 0; i < Layers.length - 1; i++){

            layers.add(new layer(Layers[i], Layers[i+1]));

        }
        layers.add(new layer(Layers[Layers.length-1], 0));

    }

    void setLayer(int index, BigDecimal[] values){

        layer l = layers.get(index);

        if(l.getNodes().size() != values.length) return;

        for(int i = 0; i < values.length; i++){

            l.getNodes().get(i).setValue(values[i]);

        }

    }


    BigDecimal getNodeValue(int layer, int index){

        return layers.get(layer).getNodeValue(index);

    }

    BigDecimal getNodeInput(int layer, int index){

        if(layer <= 0 || layer >= layers.size()) return new BigDecimal("404.404");

        layer outputs = layers.get(layer-1);

        BigDecimal out = new BigDecimal("0");

        for(node l : outputs.nodes){

            out = out.add(l.weights[index].multiply(l.getValue())).setScale(4, RoundingMode.FLOOR);

        }

        out = out.add(layers.get(layer).getNodes().get(index).Bias);

        return out;

    }

    BigDecimal sigmoid(BigDecimal x){

        BigDecimal b = new BigDecimal("1");

        BigDecimal c = new BigDecimal(1 + Math.exp(-x.doubleValue()));

        return b.divide(c, 4, RoundingMode.FLOOR);

    }
    BigDecimal sigmoidDerivative(BigDecimal num){

        return num.multiply((new BigDecimal("1").subtract(num))).setScale(4, RoundingMode.FLOOR);

    }

    BigDecimal RelU(BigDecimal x){

        if(x.doubleValue() < 0) return x.multiply(leakyInt);
        else return x;

    }

    BigDecimal RelUDerivative(BigDecimal x){

        if(x.doubleValue() < 0) return leakyInt;
        else return new BigDecimal("1");

    }

    BigDecimal leakyInt = new BigDecimal("0.1");

    void propagate(){

        for(int i = 1; i < layers.size(); i++){

            layer l = layers.get(i);

            for(int j = 0; j < l.getNodes().size(); j++){


                l.getNodes().get(j).setValue(sigmoid(getNodeInput(i, j).setScale(4, RoundingMode.FLOOR)));
                l.getNodes().get(j).setPreValue(getNodeInput(i, j).setScale(4, RoundingMode.FLOOR));

            }

        }

    }



    void calcOutputLayerError(BigDecimal[] expected){

        if(expected.length != layers.get(layers.size()-1).nodes.size()) return;

        BigDecimal[] temp = new BigDecimal[expected.length];

        for(int i = 0; i < temp.length; i++){


            BigDecimal tempError = layers.get(layers.size()-1).nodes.get(i).getValue().subtract(expected[i]);
            layers.get(layers.size()-1).nodes.get(i).setError(tempError.setScale(4, RoundingMode.FLOOR));

        }

    }

    void backPropError(){

        for(int i = layers.size()-2; i >= 0; i--){

            for(node n : layers.get(i).getNodes()){

                n.setError(new BigDecimal(0));

                for(int j = 0; j < layers.get(i+1).getNodes().size(); j++){

                    n.error = n.error.add(layers.get(i+1).getNodes().get(j).error.multiply(n.weights[j])).setScale
                            (4, RoundingMode.FLOOR);

                }

            }

        }


    }

    void updateWeightOutput(BigDecimal learningRate){

        for(int i = 0; i < layers.get(layers.size()-1).getNodes().size(); i++){

            node n = layers.get(layers.size()-1).getNodes().get(i);

            BigDecimal derivative =  sigmoidDerivative(n.value).multiply(n.getError()).multiply(learningRate);

            for(node a : layers.get(layers.size()-2).getNodes()){

                BigDecimal delta = derivative.multiply(a.getValue());
                //System.out.println(delta + " " + a.getValue());
                a.weights[i] = a.weights[i].subtract(delta);

            }

        }

    }

    void updateWeightHidden(BigDecimal learningRate){

        for(int i = layers.size() - 2; i > 0; i--){

            layer l = layers.get(i);

            for(int j = 0; j < l.getNodes().size(); j++){

                node n = l.getNodes().get(j);

                BigDecimal sumOfOuts = new BigDecimal(0);

                BigDecimal derivative = sigmoidDerivative(n.value).multiply(learningRate);

                for(int a = 0; a < n.weights.length; a++){

                    node temp = layers.get(i+1).getNodes().get(a);
                    sumOfOuts = sumOfOuts.add(temp.error.multiply(sigmoidDerivative(temp.value)).multiply
                            (n.weights[a]));
                }
                BigDecimal temp = derivative.multiply(sumOfOuts);

                for(node prevLayerNode : layers.get(i-1).getNodes()){

                    BigDecimal temp2 = temp.multiply(prevLayerNode.getValue());
                    prevLayerNode.weights[j] = prevLayerNode.weights[j].subtract(temp2);

                }


            }

        }

    }

    void backProp(BigDecimal[] expected, BigDecimal learningRate){

        calcOutputLayerError(expected);
        backPropError();
        updateWeightOutput(learningRate);
        updateWeightHidden(learningRate);

    }






}

class layer{

    ArrayList<node> nodes = new ArrayList<>();


    void print(){
        int counter = 1;
        for(node n : nodes){
            System.out.println("node: " + counter);
            n.print();
            counter++;
        }

    }

    public ArrayList<node> getNodes() {
        return nodes;
    }


    layer(int size, int NextLayer){

        for(int i = 0; i < size; i++) nodes.add(new node(new BigDecimal[NextLayer]));

    }

    BigDecimal getNodeValue(int index){

        return nodes.get(index).getValue();

    }

}

class node {

    BigDecimal Bias;
    BigDecimal value;
    BigDecimal[] weights;



    public BigDecimal getError() {
        return error;
    }

    public void setError(BigDecimal error) {
        this.error = error;
    }

    BigDecimal error = new BigDecimal("0");

    void print(){

        System.out.print("node value: " + value);

        for(int i = 0; i < weights.length; i++){

            System.out.print(" weight " + (i + 1) + ": " + weights[i] + ", ");

        }
        System.out.println();

    }
    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal[] getWeights() {
        return weights;
    }


    public BigDecimal getPreValue() {
        return PreValue;
    }

    public void setPreValue(BigDecimal preValue) {
        PreValue = preValue;
    }

    BigDecimal PreValue;

    BigDecimal[] oldWeights;

    node(BigDecimal[] Weights){

        value = new BigDecimal("0");
        weights = Weights;
        oldWeights = Weights;

        for(int i = 0; i < weights.length; i++){

            oldWeights[i] = weights[i] = BigDecimal.valueOf(Math.random()/20);

        }

        Bias = new BigDecimal(0);

    }

    void setValue(BigDecimal Value){

        value = Value;

    }

    void UpdateWeights(BigDecimal[] updates){

        for(int a = 0; a < weights.length; a++) weights[a] = weights[a].add(updates[a]);


    }

    void updateWeight(int index, BigDecimal update){

        weights[index] = weights[index].add(update);

    }

    void setWeights(BigDecimal[] Weights){

        for(int a = 0; a < weights.length; a++) weights[a] = Weights[a];


    }

    void setWeight(int index, BigDecimal value){

        weights[index] = value;

    }

    BigDecimal calcOutput(int index){

        return value.multiply(weights[index]);

    }

}
