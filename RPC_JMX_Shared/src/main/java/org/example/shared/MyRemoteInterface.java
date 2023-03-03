package org.example.shared;

import org.json.simple.JSONObject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface MyRemoteInterface extends Remote {

    ArrayList<byte[]> imageProcessor(byte[] imageInBytes, JSONObject inputJsonObject) throws RemoteException;
}
