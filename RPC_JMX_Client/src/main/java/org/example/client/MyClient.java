package org.example.client;

import org.example.shared.MyRemoteInterface;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class MyClient {
    public static void main(String[] args) {

        System.out.println("Hello client!");

        try {
            // Locate the server in the registry and create a stub for the communication
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            MyRemoteInterface stub = (MyRemoteInterface) registry.lookup("MyRemoteInterface");

            // Read the inputJson file
            String filePath = MyClient.class.getClassLoader().getResource("inputJson.json").getPath();
            FileReader reader = new FileReader(filePath);

            // Parse the JSON file into a JSONObject
            JSONParser parser = new JSONParser();
            JSONObject inputJsonObject = (JSONObject) parser.parse(reader);
            System.out.println(inputJsonObject);

            // Read the image and convert into bytes
            File file = new File("C:\\Anjali\\Proj Management\\poker.jpg");
            BufferedImage inputImage = ImageIO.read(file);
            System.out.println(inputImage.getHeight());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(inputImage, "png", baos);
            byte[] imageInBytes = baos.toByteArray();
            
            //Call remote imageProcessor method (running on server)
            ArrayList<byte[]> responseByteImages = stub.imageProcessor(imageInBytes,inputJsonObject);

            for (int i = 0; i < responseByteImages.size(); i++) {
                System.out.println(responseByteImages.get(i).length);
                File outputFile = new File("C:\\Anjali\\ProcessedImages\\Final"+i+".png");
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(responseByteImages.get(i));
            }
            
            System.out.println("Successfully saved response files");

        } catch (Exception e) {
            System.out.println("ERROR:  "+e.getMessage());
            System.out.println("Please try again");
        }
    }
}
