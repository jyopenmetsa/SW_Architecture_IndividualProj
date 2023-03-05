package org.example.server;

import org.example.server.utils.Operations;
import org.example.shared.MyRemoteInterface;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class MyServer implements MyRemoteInterface {

    static MyPropertiesReader reader = new MyPropertiesReader();

    public static void main(String[] args) {

        System.out.println("Hello Server!");
        try {
            // Read server details form properties file
            String serverPort = reader.getProperty("server.port");
            String remoteInterface = reader.getProperty("remote.interface");

            // Create object for server class and bind it to the local registry running on 1099 port
            MyServer server = new MyServer();
            MyRemoteInterface stub = (MyRemoteInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(serverPort)); // Bind the stub in the RMI registry
            registry.bind(remoteInterface, stub);
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<byte[]> imageProcessor(byte[] imageInBytes, JSONObject inputJsonObject) throws RemoteException {

        System.out.println("Operations From Client : "+inputJsonObject.get("operations"));
        String imageFormat = reader.getProperty("image.format");
        ArrayList<byte[]> responseImagesInBytes = new ArrayList<byte[]>();
        ByteArrayInputStream bais = new ByteArrayInputStream(imageInBytes);
        try {
            BufferedImage image = ImageIO.read(bais);

            JSONArray inputOperations = (JSONArray) inputJsonObject.get("operations");
            Operations operation = new Operations();

            for (int i = 0; i < inputOperations.size(); i++) {
                System.out.println(inputOperations.get(i));
                JSONObject action = (JSONObject) inputOperations.get(i);
                String operationName = action.get("name").toString();

                switch (operationName) {

                    case "grayscale":
                        System.out.println("Converting into Grayscale");
                        image = operation.toGrayscale(image);
                        break;

                    case "rotate":
                        System.out.println("Rotating image with given direction");
                        image = operation.toRotate(image, action.get("parameter").toString());
                        break;

                    case "rotate_angle":
                        System.out.println("Rotating image with specified angle");
                        image = operation.toRotateAngle(image, ((Long) action.get("parameter")).intValue());
                        break;

                    case "resize":
                        System.out.println("Resizing image");
                        image = operation.toResize(image,
                                ((Long) action.get("width")).intValue(), ((Long) action.get("height")).intValue());
                        break;

                    case "flip":
                        System.out.println("Performing flip");
                        image = operation.toFlip(image, action.get("parameter").toString());
                        break;

                    case "thumbnail":
                        System.out.println("Creating thumbnail");
                        BufferedImage thumbnailImage = operation.toThumbnail(image);
                        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                        ImageIO.write(thumbnailImage, imageFormat, thumbnailStream);
                        byte[] thumbnailBytes = thumbnailStream.toByteArray();
                        responseImagesInBytes.add(thumbnailBytes);
                        thumbnailStream.close();
                        break;

                    default: System.out.println("default");
                    break;
                }
            }

            ByteArrayOutputStream finalImageStream = new ByteArrayOutputStream();
            ImageIO.write(image, imageFormat, finalImageStream);
            byte[] finalImageBytes = finalImageStream.toByteArray();
            responseImagesInBytes.add(finalImageBytes);

            finalImageStream.close();
            bais.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Sending response to client");
        return responseImagesInBytes;
    }

}