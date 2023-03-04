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
            
            Scanner FileNameScanner = new Scanner(System.in);

            System.out.print("Enter your image file path: ");
            String FilePath = FileNameScanner.nextLine();
            System.out.println(FilePath);

            Scanner Jsonscanner = new Scanner(System.in);
            System.out.print("Enter input operations JSON data: ");
            String jsonString = "";

            // Read input lines until an empty line is entered
            while (Jsonscanner.hasNextLine()) {
                String line = Jsonscanner.nextLine();
                if (line.isEmpty()) {
                    break;
                }
                jsonString += line;
            }
            
            // Parse the JSON file into a JSONObject
            JSONParser parser = new JSONParser();
            JSONObject inputJsonObject = (JSONObject) parser.parse(jsonString);
            System.out.println(inputJsonObject);

            // Read the image and convert into bytes
            File file = new File(FilePath);
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
