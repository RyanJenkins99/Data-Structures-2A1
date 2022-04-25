package sample;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;


import java.io.File;
import java.util.ArrayList;


public class Controller {



    @FXML
    public ImageView imageView,imageBlackandWhite;
    public ScrollBar scrollBright;
    public Label lblimagedetails;
    public Label lblimagesize;
    public RadioButton redF, greenF, blueF;


    Image originalImage;

    int[] disjointSet;
    int imageHeight, imageWidth;
    int[] sortedCluster;

    private static final boolean DEBUG = true;


    public void handleImageOpenButton(ActionEvent a) throws Exception {
        if (DEBUG) System.out.println("HandleImageOpenButton");
        Image i= new Image("file:/C:/Users/KinimoD/Pictures/Saved Pictures/indexcolors.jpg");
        imageView.setImage(i);
        originalImage = i;

        imageDetails();
        // initialize array
        disjointSet =new int[(int) (i.getWidth()*i.getHeight())];
        imageHeight = (int) i.getHeight();
        imageWidth = (int) i.getWidth();



    }
    public void imageDetails(){
        lblimagedetails.setText("image Name- " + originalImage.getUrl()+"\n" + "image Height- " + originalImage.getHeight() +"image Width" + originalImage.getWidth());

    }
/*
    public void imageSize(){
        lblimagesize.setText("image Width- " + originalImage.getWidth();
    }*/

    public void makeBrighter(){
        System.out.println(scrollBright.getValue());
        ColorAdjust ca=new ColorAdjust();
        ca.setBrightness(scrollBright.getValue());
        imageView.setEffect(ca);
    }




    public void handleImageOpenImageFromFinder(ActionEvent e)throws Exception{
        FileChooser fc = new FileChooser();
        Window ownerWindow = null;
        File selectedFile = fc.showOpenDialog(ownerWindow);

        if (selectedFile != null) {
            System.out.println(selectedFile.toURI().toString());
            Image image = new Image(selectedFile.toURI().toString(), 150, 150, true, true);
            imageView.setImage(image);
            disjointSet =new int[(int) (image.getWidth()*image.getHeight())];
            imageHeight = (int) image.getHeight();
            imageWidth = (int) image.getWidth();
        } else {
            System.out.println("Not a valid file.");
        }

    }

    public int numberOfFruit(){
        // ArrayList<Pair<Integer, Integer>> xys = new ArrayList<>();//making a pair arraylist that holds pairs
        Image bwImage = imageBlackandWhite.getImage();
        PixelReader pixelReader = bwImage.getPixelReader();

        //Create Writable Image
        WritableImage wImage = new WritableImage(
                (int) bwImage.getWidth(),
                (int) bwImage.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();

        // c is count that will represent disjoint sets
        int c = 0;
        for(int row = 0; row < bwImage.getHeight(); row++) {
            //System.out.println();
            //  System.out.print(row);
            for (int col = 0; col < bwImage.getWidth(); col++) {
                //  System.out.print(col);            x, y
                Color color = pixelReader.getColor(col, row);
                if(color.equals(Color.WHITE)){
                    // white pixel means fruit therfore gets value
                    disjointSet[c] =c;
                }
                else{
                    disjointSet[c] = -1;
                }

                c++;
            }
        }

        visualizeDS();
        unionFruitPixels();
        visualizeDS();

//        UnionFind uf = new UnionFind(xys.size());//comes from the unionfind class
//        for(int i = 0; i < xys.size(); i++){
//            System.out.println(xys.get(i).getKey()+ " : " + xys.get(i).getValue() +" : " + xys.get(i) );//
//            if(uf.find(xys.get(i).getKey()) == xys.get(i).getValue()) continue;//
//            uf.union(xys.get(i).getKey(), xys.get(i).getValue());
//        }
//        System.out.println("8==============================================================================>");
//        for(int i = 0; i < xys.size(); i++){
//            //System.out.println(xys.get(i).getKey()+ " : " + xys.get(i).getValue() +" : " + xys.get(i) );//
//            System.out.println(uf.find(xys.get(i).getKey()));//
//
//        }
//        System.out.println(uf.count());
        return 0;
    }


    public void visualizeDS(){
        System.out.println();
        int counter =0;
        for(int i : disjointSet){
            if(counter==imageWidth-1){
                System.out.print(i);
                System.out.println();
                counter =0;
            }
            else{
                System.out.print(i);
            }
            counter++;
        }
    }


    // This method creates disjoint set
    // This is the part where you look up and down left and right to union them.
    public void unionFruitPixels() {
        for (int i=0; i<disjointSet.length; i++) {
            if (pixelIsWhite(disjointSet,i)) {

                int top = i-imageWidth;
                int right = i+1;
                int bottom = i+imageWidth;
                int left = i-1;


                if (!atTopEdge(i) && pixelIsWhite(disjointSet,top))
                    unionPixels(disjointSet,i, top);
                if (!atRightEdge(i) && pixelIsWhite(disjointSet,right))
                    unionPixels(disjointSet,i, right);
                if (!atBottomEdge(i) && pixelIsWhite(disjointSet,bottom))
                    unionPixels(disjointSet,i, bottom);
                if (!atLeftEdge(i) && pixelIsWhite(disjointSet,left))
                    unionPixels(disjointSet,i, left);
            }
        }
    }



    public boolean pixelIsWhite(int[] array,int i) {
        // -1 means black
        return array[i] != -1;
    }

    // checking going out of bounds on the image
    public boolean atTopEdge(int i) {
        return i-imageWidth<0;
    }

    public boolean atRightEdge(int i) {
        return (2*(i+1)) % imageWidth == 0;
    }

    public boolean atBottomEdge(int i) {
        return i+imageWidth > imageWidth*imageHeight-1;
    }

    public boolean atLeftEdge(int i) {
        return i%imageWidth==0;
    }

    public void unionPixels(int[] array,int a, int b) {
        if(find(array, a) < find(array, b))
            quickUnion(array, a, b);
        else quickUnion(array, b, a);
    }

    public void quickUnion(int[] array, int p, int q) {
        array[find(array, q)] = find(array, p);
    }

    public int find(int[] a, int id) {

        return a[id] == id ? id : (a[id] = find(a, a[id]));
    }

//    public void drawRectangle() {
//        int maxHeight, minHeight, maxWidth, minWidth;
//        int fruitCounter=0;
//
//
//        for (int i = 0; i < sortedCluster.length; i++) {
//            maxHeight = sortedCluster[i] / imageWidth;
//            minHeight = sortedCluster[i] / imageWidth;
//            maxWidth = sortedCluster[i] % imageWidth;
//            minWidth = sortedCluster[i] % imageWidth;
//
//            for (int j = 0; j < disjointSet.length; j++) {
//                if (find(disjointSet, j) == sortedCluster[i]) {
//                    if (j / imageWidth < maxHeight) {
//                        maxHeight = j / imageWidth;
//                    }
//                    if (j / imageWidth > minHeight) {
//                        minHeight = j / imageWidth;
//                    }
//                    if (j % imageWidth < minWidth) {
//                        minWidth = j % imageWidth;
//                    }
//                    if (j % imageWidth > maxWidth) {
//                        maxWidth = j % imageWidth;
//                    }
//                }
//            }


    
    



    public void handleImageButtonBlackandWhite(ActionEvent i) throws Exception {
        System.out.println("handleRedImageButton");
        Image image = imageView.getImage();
        if (image != null) {
            PixelReader pixelReader = image.getPixelReader();

            //Create Writable Image
            WritableImage wImage = new WritableImage(
                    (int) image.getWidth(),
                    (int) image.getHeight());
            PixelWriter pixelWriter = wImage.getPixelWriter();

            System.out.println("Image height" + image.getHeight() + "Image width");

            for(int row = 0; row < image.getHeight(); row++) {
                for (int col = 0; col < image.getWidth(); col++) {
                    Color color = pixelReader.getColor(col, row);
                    Double redColor = color.getRed();
                    Double greenColor = color.getGreen();
                    Double blueColor = color.getBlue();
//                    if(redColor*255>greenColor && greenColor*255<100 && blueColor*255<100)
//                        pixelWriter.setColor(col,row,Color.WHITE);
//                    else pixelWriter.setColor(col,row,Color.BLACK);



                            double valueOfColour = color.getRed() + color.getBlue() + color.getGreen();

                    Color borW;

                    if (redF.isSelected())
                    {
                        if(redColor>0.61 && greenColor<0.33 && blueColor<0.33)
                        {
                            pixelWriter.setColor(col,row,Color.WHITE);
                            System.out.println(redColor + " " + greenColor + " " + blueColor + "");
                        }
                        else pixelWriter.setColor(col,row,Color.BLACK);
//
                    }


                    if (greenF.isSelected())
                    {
                        if(redColor>0.25 && greenColor<0.90 && blueColor<0.60)
                        {
                            pixelWriter.setColor(col,row,Color.WHITE);
                            System.out.println(redColor + " " + greenColor + " " + blueColor + "");
                        }
                        else pixelWriter.setColor(col,row,Color.BLACK);
//
                    }
//
                    if (blueF.isSelected())
                    {
                        if(redColor>0.12 && greenColor<0.38 && blueColor<0.99)
                        {
                            pixelWriter.setColor(col,row,Color.WHITE);
                            System.out.println(redColor + " " + greenColor + " " + blueColor + "");
                        }
                        else pixelWriter.setColor(col,row,Color.BLACK);
//
                    }



//                        if (borW = (color.getHue() <= 206 && color.getHue() >= 195 && color.getSaturation() <= 0.4) ? Color.WHITE : Color.BLACK;
//
//
//                            if(borW = (color.getHue() <= 360 && color.getHue() >= 345 && color.getSaturation() > 0.85) ? Color.WHITE : Color.BLACK;
//

                            //






                }
            }
            imageBlackandWhite.setImage(wImage);
        }

    }


    public void selectFruit(ActionEvent actionEvent) {
        if (redF.isSelected()) {
            System.out.println("hello");
              greenF.setSelected(false);
              blueF.setSelected(false);
        }

        if (greenF.isSelected()) {
            System.out.println("bonjour");
            redF.setSelected(false);
            blueF.setSelected(false);
        }

        if (blueF.isSelected()) {
            System.out.println("hey");
            greenF.setSelected(false);
            redF.setSelected(false);
        }
//everytime remember to select and deselect the radio buttons
    }

//    /** Counts pixels in a disjoint set
//     */
//    public int pixelIsWhite(int i) {
//        int pixelCount = 0;
//        for (int j = 0; j < disjointSet.length; j++) {
//            if (find(disjointSet, j) == i) {
//                pixelCount++;
//            }
//        }
//        return pixelCount;
//    }

//    public int getSetSize(DisjointSetNode[] disjointSetNodes, int rootId) {
//        int size = 0;
//        for (int i = 0; i < disjointSetNodes.length; i++) {
//            if (ds[i].data != -1) {
//                if (find(disjointSetNodes, i).data.equals(rootId)) size = size + 1;
//            }
//        }
//        return size;
//    }
}