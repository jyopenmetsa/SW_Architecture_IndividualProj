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

    public static void main(String[] args) {

        System.out.println("Hello Server!");
        try {
            MyServer server = new MyServer();
            MyRemoteInterface stub = (MyRemoteInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(1099); // Bind the stub in the RMI registry
            registry.bind("MyRemoteInterface", stub);
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<byte[]> imageProcessor(byte[] imageInBytes, JSONObject inputJsonObject) throws RemoteException {
        System.out.println(imageInBytes.length);
        System.out.println(inputJsonObject.get("operations"));
        ArrayList<byte[]> responseImagesInBytes = new ArrayList<byte[]>();
        ByteArrayInputStream bais = new ByteArrayInputStream(imageInBytes);
        try {
            BufferedImage image = ImageIO.read(bais);
            System.out.println(image.getHeight());

            JSONArray inputOperations = (JSONArray) inputJsonObject.get("operations");
            Operations operation = new Operations();

            for (int i = 0; i < inputOperations.size(); i++) {
                System.out.println(inputOperations.get(i));
                JSONObject action = (JSONObject) inputOperations.get(i);
                String operationName = action.get("name").toString();

                switch (operationName) {

                    case "grayscale":
                        System.out.println("Grayscale");
                        image = operation.toGrayscale(image);
                        System.out.println(image.getHeight());
                        break;

                    case "rotate":
                        System.out.println("rotate");
                        image = operation.toRotate(image, action.get("parameter").toString());
                        System.out.println(image.getHeight());
                        break;

                    case "rotate_angle":
                        System.out.println("rotateAngle");
                        image = operation.toRotateAngle(image, ((Long) action.get("parameter")).intValue());
                        System.out.println(image.getHeight());
                        break;

                    case "resize":
                        System.out.println("resize");
                        image = operation.toResize(image,
                                ((Long) action.get("width")).intValue(), ((Long) action.get("height")).intValue());
                        break;

                    case "flip":
                        System.out.println("flip");
                        image = operation.toFlip(image, action.get("parameter").toString());
                        break;

                    case "thumbnail":
                        System.out.println("thumbnail");
                        image = operation.toThumbnail(image);
                        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", thumbnailStream);
                        byte[] thumbnailBytes = thumbnailStream.toByteArray();
                        responseImagesInBytes.add(thumbnailBytes);
                        thumbnailStream.close();
                        break;

                    default: System.out.println("default");
                    break;
                }
                System.out.println("end of switch");
            }

            ByteArrayOutputStream finalImageStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", finalImageStream);
            byte[] finalImageBytes = finalImageStream.toByteArray();
            responseImagesInBytes.add(finalImageBytes);

            finalImageStream.close();
            bais.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return responseImagesInBytes;
    }

}