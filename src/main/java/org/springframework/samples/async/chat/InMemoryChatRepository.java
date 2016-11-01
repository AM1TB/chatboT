
package org.springframework.samples.async.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class InMemoryChatRepository implements ChatRepository {

    
	private final List<String> messages = new CopyOnWriteArrayList<String>();

	int port=8689;	    
        @Override
	public List<String> getMessages(int index) {
		if (this.messages.isEmpty()) {
			return Collections.<String> emptyList();
		}
		Assert.isTrue((index >= 0) && (index <= this.messages.size()), "Invalid message index");
		return this.messages.subList(index, this.messages.size());
	}

        @Override
	public void addMessage(String message) {
            InetAddress addr;
            DatagramSocket clientSocket;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                int tb=message.lastIndexOf("] ");
                String uname=message.substring(0, tb+1);
                message=message.substring(tb+2);
                
//                this.messages.add(uname);
//                this.messages.add(message);
                
                clientSocket = new DatagramSocket();
                addr= InetAddress.getByName("localhost");
                byte[] toSend = new byte[1024];
                byte[] toRecv = new byte[1024];
                toSend = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(toSend, toSend.length, addr, port);
                clientSocket.send(sendPacket);
                
                
                //get reply                
                DatagramPacket receivePacket = new DatagramPacket(toRecv, toRecv.length);
                clientSocket.receive(receivePacket);
                String asliMsg = new String(receivePacket.getData());
                
                
                this.messages.add(uname+asliMsg);
            } catch (SocketException ex) {
                Logger.getLogger(InMemoryChatRepository.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(InMemoryChatRepository.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(InMemoryChatRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
		     
            
	}

}
