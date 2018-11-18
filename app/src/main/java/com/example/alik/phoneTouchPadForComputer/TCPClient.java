package com.example.alik.phoneTouchPadForComputer;

import android.os.AsyncTask;

import java.io.*;
import java.net.*;

class TCPClient {

	public static DatagramSocket clientSocket;
	public static InetAddress IPAddress;
	public static int port;
	public static byte[] sendData = new byte[1024];
	public static byte[] receiveData = new byte[1024];

	public static int MOVE_EVENT = 1;
	public static int LEFT_CLICK_EVENT = 2;
	public static int RIGHT_CLICK_EVENT = 3;
}

class ConnectToServer extends AsyncTask<Void, Void, Void> {


	@Override
	protected Void doInBackground(Void... voids) {
		try {
			TCPClient.clientSocket = new DatagramSocket();
			TCPClient.IPAddress = InetAddress.getByName("10.100.102.8");
			TCPClient.port = 9876;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

}

class SendDataToServer extends AsyncTask<String, Void, Void> {


	@Override
	protected Void doInBackground(String... params) {
		try {
		    System.out.println(params[0]);
			TCPClient.sendData = params[0].getBytes();
			DatagramPacket packetToSend = new DatagramPacket(TCPClient.sendData,
					TCPClient.sendData.length, TCPClient.IPAddress, TCPClient.port);
			TCPClient.clientSocket.send(packetToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}

class UdpCloseConnection extends AsyncTask<Void, Void, Void> {
	@Override
	protected Void doInBackground(Void... voids) {
//			InetAddress IPAddress = InetAddress.getByName("localhost");
			TCPClient.clientSocket.close();
		return null;
		}

}
