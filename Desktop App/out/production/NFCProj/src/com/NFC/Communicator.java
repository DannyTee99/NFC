package com.NFC;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.smartcardio.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Communicator extends Thread
{
    private CardTerminal terminal;
    private Card deviceAsCard;
    private CardChannel channel;

    private byte[] selectAidApdu;
    private ResponseAPDU readerResponse;
    private byte[] readerResponseBytes={};
    private byte[] readerSenderBytes={};

    private final byte[] CLA_INS_P1_P2 = { 0x00, (byte)0xA4, 0x04, 0x00 };
    private final byte[] AID_ANDROID = { (byte)0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
    private final byte[] DATA_EXCHANGE_ORDER={ (byte)0xD4, (byte) 0x4A, 0x01, 0x00};

    private byte[] firstMessage;
    private ByteArrayMap MESSAGES= new ByteArrayMap();

    private String[] statusInformation={"Text","Some text",",Place Holder 1","Place holder 2"};
    private byte[] myBytes;

    private String DU2="";
    private String DP2="";


    private AuthentificationThread autTh=null;
    //private calculation cal = null;
    public boolean done=false;


    //initiates a bidirectional connection with an Android device
    public Communicator() throws CardException, IOException
    {
        //Check status
        myBytes=convertToBytes(statusInformation);

        // Find NFC Reader
        terminal = getTerminal();

        //.sendable messages without data
        firstMessage=DictInf.RAskForIdentModeid;	//receive initialisation message -> send first command
        MESSAGES.put(DictInf.HcommitWaitid, DictInf.Rwaitid);	//Wait message (if thread is working in the background
        MESSAGES.put(DictInf.HIdentModeSelectedid, DictInf.RAskForU2id);//When you receive the message Mode selected, send a request to get data


        //sendable messages with data


    }

    private byte[] processDataIfNecessary(byte[] readerResponseAsBytes) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException
    {
        //ALWAYS STARTS WHEN A READER ARRIVES (readerResponseAsBytes)

        //1.Interpret input bytes as a string (for comparability)
        String inputStr=new String(readerResponseAsBytes, "US-ASCII");
        try{

            //1. Receive data transfer completed
            if(Arrays.equals(DictInf.HDoneWithIdid,readerResponseAsBytes))
                handleIdConversationDone();

            //2. Authentication thread finished with authentication, or exists at all -> send corresponding messages
            byte[] authState=checkAuthentificationComplete();
            if(authState!=null)
                return authState;

            //4. Receive User name DU2
            if(inputStr.substring(0,3).equals("U1:"))
            {
                DU2=inputStr.substring(4,inputStr.length());
                UserKey.amount = Integer.parseInt(DU2);
                return DictInf.RAskForP2id;
            }

            //5. Receive Password DP2
            if(inputStr.substring(0,3).equals("P1:"))
            {
                DP2=inputStr.substring(4,inputStr.length());
                System.out.println("Amount FOUND: "+DP2);
                UserKey.key = DP2;
                System.out.println("User Key "+ UserKey.key);

                //5. Start authentication thread to check the validity of the account
                autTh= new AuthentificationThread(DU2, DP2);
                autTh.start();

                calculation cal = new calculation();
                cal.AmountCal();
                cal.Amountadd();
                cal.UpdateWithdraw();
                //in the meantime, the two of them send back and forth
                //return DictInf.Rwaitid;


            }




        }
        catch(Exception e)
        {
            DU2="";
            DP2="";
        }

        return null;
    }

    private void handleIdConversationDone() throws CardException, InterruptedException, IOException
    {
        //1. break connection and look for a new connection if it was not successful
        System.out.println("Done with data transfer");

        //2. Successful or not?
        if(autTh.validUser)
        {

            System.out.println("User: '"+ DU2 +"' could be successfully identified.");
            autTh.sleep(10000);
            //Establish initial state
            readerSenderBytes=null;
            readerResponseBytes=null;
            readerResponse=null;
            autTh=null;

        }
        else
        {
            System.out.println("User could not be identified. New trial");

            //Establish initial state
            readerSenderBytes=null;
            readerResponseBytes=null;
            readerResponse=null;
            autTh=null;
        }
    }

    private byte[] checkAuthentificationComplete() throws IOException, CardException, InterruptedException
    {
        //Tests whether an authentication already exists and whether it was valid
        if(autTh!=null)
        {
            if(autTh.done)
            {
                if(autTh.validUser)
                {
                    //Send message, users could find everything good
                    System.out.println("USER FOUND");

                    //Return message with all status information
                    return DictInf.RokGoodid;

                    //return sendMessagesWithStatusInfos();
                }
                else
                {
                    //could not find a user with this data:O
                    System.out.println("THIS USER WAS NOT FOUND");

                    return DictInf.RokNotGoodid;
                }
            }
        }
        return null;
    }

    private byte[] sendMessagesWithStatusInfos() throws IOException
    {

        //2.Send finished encoded data
        byte[] prefix="S1: ".getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(prefix);
        //outputStream.write(myBytes);
        byte[] SStatusBytes = outputStream.toByteArray();

        return SStatusBytes;
    }

    private byte[] getAnswer() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, IOException
    {


        //  process incoming data here
        byte[] data=processDataIfNecessary(readerResponseBytes);

        // send data from processedData if necessary
        if(data!=null)
        {
            return data;
        }

        // Send message depending on  input
        try
        {

            byte[] messageToSens= MESSAGES.get(readerResponseBytes);
            printByteArray(messageToSens);
            return messageToSens;
        }
        catch(Exception e)
        {

            return firstMessage;
        }
    }

    //app wait till phone is on scanner, check AID and establish communication
    public void run()
    {
        //1.check whether phone is available
        try {waitTillDevicePresent();}catch(CardException | InterruptedException e1){}

        //2. Establish connection
        establishConnection();

        //3. ask AID of the app
        try {selectAppByAID();} catch (CardException | InterruptedException e2){}

        //4. actual communication
        while(true)
        {try {communicate();} catch (CardException | InterruptedException e) {e.printStackTrace();}}
    }

    private void communicate() throws CardException, InterruptedException
    {
        //1. check if the device is ready for use
        checkIfReady();

        //2. Send message, depending on the input
        sendAndReceiveMessages();
    }


    private CardTerminal getTerminal() throws CardException
    {
        // Check Terminal Factory
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        System.out.println("Terminals: " + terminals);

        // Check terminal
        CardTerminal terminal = terminals.get(0);
        return terminal;
    }

    private void sendAndReceiveMessages()
    {
        //Sends a message to the reader
        try
        {
            //1. Determine the message to be sent
            readerSenderBytes = getAnswer();

            //2. Prepare message for reader
            byte[] preparedMessage=getMessage(readerSenderBytes);

            //3. Send message as Command APDU
            readerResponse = channel.transmit(new CommandAPDU(preparedMessage));

            //4.read out reader answer
                readerResponseBytes= processResponse(readerResponse.getBytes());

            logStatus();
        }
        catch(Exception e2)
        {
            handleConnectionLost();
        }
    }

    private void logStatus() throws UnsupportedEncodingException
    {
        //1.Prepare
        String responseAsString = new String(readerResponseBytes, "US-ASCII");
        String inputAsString = new String(readerSenderBytes, "US-ASCII");

        //2. Input
        System.out.print("\n\nSend: \n  String: " + inputAsString +" \n  Bytes: ");
        printByteArray(readerSenderBytes);

        //3. Output
        System.out.print("\nReceive: \n  String: " + responseAsString +" \n  Bytes: ");
        printByteArray(readerResponseBytes);
    }

    private void handleConnectionLost()
    {
        //Tries to connect again if it breaks
        try
        {
            System.out.println("NEED TO RECONNECT");
            reConnect();
        }
        catch(Exception ee)
        {
        }
    }

    private byte[] processResponse(byte[] rawMessage)
    {
        //Extracts the bytes with the sent data from the reader response

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        for(int i=0;i<rawMessage.length;i++)
        {
            if(i==(rawMessage.length-1) || i==(rawMessage.length-2) || i==0 || i==1 || i==2)
            {
                continue;
            }
            outputStream.write(rawMessage[i]);
        }
        byte [] processedBytes = outputStream.toByteArray( );

        return processedBytes;

    }

    private byte[] getMessage(byte[] message) throws IOException
    {
        byte[] readerPreBytes={ (byte)0xff, 0x00, 0x00, 0x00};
        byte[] messageLength=lenByte(message.length+3);     // due to the length of the data exchange format
        byte[] dataExchangeFormat= {(byte) 0xD4, 0x40, 0x01};


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(readerPreBytes);
        outputStream.write(messageLength);
        outputStream.write(dataExchangeFormat);
        outputStream.write(message);

        byte [] preparedMessage = outputStream.toByteArray( );

        return preparedMessage;
    }

    private byte[] lenByte(int len)
    {
        byte[] bytes = ByteBuffer.allocate(4).putInt(len).array();
        byte[] byteArray={bytes[3]};

        return byteArray;

    }

    private void checkIfReady() throws CardException, InterruptedException
    {
        // card is still available?
        //if not reconnect
        waitTillDevicePresent();

        try
        {
            ResponseAPDU readerResponseStatus = channel.transmit(new CommandAPDU(DATA_EXCHANGE_ORDER));
        }
        catch(Exception c)
        {
            reConnect();
        }
    }

    private void waitTillDevicePresent() throws CardException, InterruptedException
    {
        while(!terminal.isCardPresent())
        {
            System.out.println("No card recognized on device");
            Thread.sleep(200);
        }

    }

    private void establishConnection()
    {
        selectAidApdu = createSelectAidApdu(AID_ANDROID);

        boolean connectionOk=false;
        while(!connectionOk)
        {
            try
            {
                deviceAsCard=terminal.connect("T=1"); // reader

                channel = deviceAsCard.getBasicChannel();

                connectionOk=true;
            }
            catch(Exception xx)
            {
                System.out.println("WAITING FOR CONNECTION");
            }
        }
    }

    private void selectAppByAID() throws CardException, InterruptedException
    {
        System.out.print("\ninput: ");
        printByteArray(selectAidApdu);

        boolean AidSelectionSuccess=false;
        String s="";
        readerResponse=null;
        while(!AidSelectionSuccess)
        {
            try
            {
                //1. Try to reach the given app via AID
                readerResponse = channel.transmit(new CommandAPDU(selectAidApdu));
                byte[] test=readerResponse.getBytes();

                System.out.print("\nAID Selection Successful. Connected!\nResponse after AID Selection: ");
                s = new String(readerResponse.getBytes(), "US-ASCII");
                System.out.print(s);

                //2. if successful, the program continues
                AidSelectionSuccess=true;
            }
            catch(Exception ef)
            {
                // If the selection unsuccessful try again
                deviceAsCard.disconnect(true);
                reConnect();
                System.out.print("\nNo AID available. No successful connection!");
            }
        }
    }

    private void reConnect() throws CardException, InterruptedException
    {
        waitTillDevicePresent();

        //2.Reconnect
        establishConnection();

        //3. AID
        selectAppByAID();
    }

    private byte[] createSelectAidApdu(byte[] aid) {
        byte[] result = new byte[6 + aid.length];
        System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
        result[4] = (byte)aid.length;
        System.arraycopy(aid, 0, result, 5, aid.length);
        result[result.length - 1] = 0;
        return result;
    }

    private void printByteArray(byte[] input)
    {
        for (int j=0; j<input.length; j++)
        {
            System.out.print("0x");
            System.out.format("%02X ", input[j]);
        }
    }

    private  String[] convertToStrings(byte[] bytesStrings) throws UnsupportedEncodingException {

        List<String> outStrings = new ArrayList<String>();

        int tmp=0;
        int elemCnt=0;
        byte[] lenByte= new byte[4];
        int curLen=0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        boolean first=true;
        for(int i=0;i<bytesStrings.length;i++)
        {
            if(tmp==0)
            {
                lenByte[tmp]=bytesStrings[i];
                outputStream = new ByteArrayOutputStream( );
                tmp++;
                continue;
            }

            if(tmp==1 || tmp==2)
            {
                lenByte[tmp]=bytesStrings[i];
                tmp++;
                continue;
            }
            if(tmp==3)
            {
                lenByte[tmp]=bytesStrings[i];
                ByteBuffer wrapped = ByteBuffer.wrap(lenByte);
                curLen = wrapped.getInt();
                elemCnt=0;
                tmp++;
                continue;
            }

            if(elemCnt<curLen)
            {
                outputStream.write(bytesStrings[i]);
            }
            if(elemCnt==curLen)
            {
                outStrings.add(new String(outputStream.toByteArray(), "US-ASCII"));

                tmp=0;
                elemCnt=-1;
                outputStream = new ByteArrayOutputStream( );

                tmp=0;
                lenByte[tmp]=bytesStrings[i];
                outputStream = new ByteArrayOutputStream( );
                tmp++;
                continue;
            }

            elemCnt++;
        }

        outStrings.add(new String(outputStream.toByteArray(), "US-ASCII"));

        return (String[]) outStrings.toArray(new String[outStrings.size()]);

    }

    private byte[] convertToBytes(String[] strings) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        for (int i=0;i<strings.length;i++)
        {
            String curStr=strings[i];

            byte[] lengByte=lenByte4(curStr.length());


            byte[] data = curStr.getBytes();

            outputStream.write(lengByte);
            outputStream.write(data);
        }

        byte [] preparedMessage = outputStream.toByteArray();

        return preparedMessage;
    }

    private byte[] lenByte4(int len)
    {
        byte[] bytes = ByteBuffer.allocate(4).putInt(len).array();
        return bytes;

    }
}
