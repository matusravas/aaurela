package nsoric.server.objects;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


public class ClientSocket {

    private static final String VERSION = "version";
    private static final String USER = "user";
    private static final String PASSWORD = "pass";
    private static final String OBJECT = "object";
    private static final String REQUEST = "request";
    private static final String SUBJECT = "subject";
    private static final String CLIENT = "client";
    private static final String SUBJECT_PARAMS = "subject_param";
    private static final int TIME_OUT_SOCKET = 10 * 1000;
    private static final int TIME_OUT_VERIFICTION = 2 * 1000;

    //verzia API protokolu
    private static final String PROTOCOL_VERSION = "1";

    //webova adresa servera
    String host;
    //komunikacny port
    int port;
    //prihlasovacie meno
    private final String login;
    //zahashovane heslo md5
    private final String hashedpass;
    //informacny string pre dany connect
    private final String client;
    //canConnect=true ak je mozne spojenie so serverom
    private boolean canConnect;
    private Socket socket;

    private DataOutputStream socketOut;
    private BufferedReader socketIn;


    /**
     * Konstruktor
     *
     * @param login  meno
     * @param pass   heslo
     * @param server nazov serveru
     * @param port   cislo portu
     */
    public ClientSocket(String login, String pass, String server, int port, String client) {
        this.host = server;
        this.port = port;
        this.login = login;
        this.hashedpass = pass;
        this.client = client;
    }

    /**
     * Metoda overujuca ci existuje a funguje server a port.
     *
     * @param server nazov serveru pre pripojenie
     * @param port   cislo portu pre pripojenie
     * @throws IOException ak nie je mozne pripojenie na server
     */
    public static void isServerAvailable(String server, int port) throws IOException {
        SocketAddress sa = new InetSocketAddress(server, port);
        Socket s = new Socket();
        s.connect(sa, TIME_OUT_VERIFICTION);
        s.close();
    }

    /**
     * Metoda, ktora sluzi pre overenie ci ma pouzivatel pristup k sieti.
     *
     * @return true-ak existuje pripojenie, false-neexistuje
     */
    public static boolean hasNetworkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isAvailable();
    }

    /**
     * Metoda, ktora otvara spojenie so serverom.
     *
     * @throws IOException ak nie je mozne pripojenie na server
     */
    private void open() throws IOException {
        try {
            socket = new Socket();
            SocketAddress sa = new InetSocketAddress(this.host, this.port);
            socket.connect(sa, TIME_OUT_SOCKET);
            socketOut = new DataOutputStream(socket.getOutputStream());
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            canConnect = true;
        } catch (IOException ex) {
            canConnect = false;
            ex.getMessage();
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Metoda, ktora ukoncuje spojenie so serverom.
     *
     * @throws IOException ak nie je mozne ukoncit pripojenie
     */
    public final void close() throws IOException {
        try {
            this.socket.close();
            this.socketOut.close();
            this.socketIn.close();
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Pripraví a odošle žiadosť na sensorical server.
     *
     * @param obj     čast OBJECT v žiadosti
     * @param req     čast REQUEST v žiadosti
     * @param subj    čast SUBJECT v žiadosti
     * @param subjpar zoznam nepovinných parametrov SUBJECT_PARAM
     * @return odpoveď zo sensorical servera
     */
    String sendRequest(String obj, String req, String subj, List<String> subjpar) throws IOException {
        JSONObject packet = new JSONObject();
        String resp;
        try {
            packet.put(VERSION, PROTOCOL_VERSION);
            packet.put(USER, this.login);
            packet.put(PASSWORD, this.hashedpass);
            packet.put(OBJECT, obj);
            packet.put(REQUEST, req);
            packet.put(SUBJECT, subj);
            packet.put(CLIENT, this.client);
            JSONArray param;
            if (subjpar == null || subjpar.isEmpty()) {
                param = new JSONArray();
                param.put(new JSONArray());
            } else {
                param = new JSONArray();
                param.put(new JSONArray(subjpar));
            }

            packet.put(SUBJECT_PARAMS, param);
        } catch (JSONException ex) {
            throw new IOException(ex.getMessage());
        }
        try {
            resp = sendSocketRequest(packet.toString());

        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        }
        return resp;
    }

    /**
     * Odošle na server žiadosť.
     *
     * @param paket reťazec vo formáte JSON
     * @return vráti odpoveď servera
     */
    private String sendSocketRequest(String paket) throws IOException {
        String s0;
        String response;
        paket = compress(paket);
        //dlzka paketu po kompresii
        int plen = paket.length();
        this.open();
        int i = 0;
//        int chunk = 8192; //povodna velkost chunk
        int chunk = 1024;
        while (plen > 0) {
            if (plen > chunk) {
                s0 = paket.substring(i * chunk, (i + 1) * chunk);

            } else {
                s0 = paket.substring(i * chunk);
            }
            plen -= chunk;
            i++;
            socketOut.writeBytes(s0);
            socketOut.flush();
        }
        socketOut.flush();
        int c;
        StringBuilder socketResult = new StringBuilder();
        do {
            c = socketIn.read();

            socketResult.append((char) c);
            if (c == '.') {
                break;
            }
        } while (c != -1);
        response = decompress(socketResult.toString());
        this.close();
        return response;
    }

    /**
     * Zakomprimovanie dat do Base64.
     *
     * @param inputString poziadavka String
     * @return zakomprimovana poziadavka
     */
    private String compress(String inputString) {
        String outputString;

        byte[] decodedByteArray = inputString.getBytes();
        // Compress the bytes
        Deflater compresser = new Deflater();
        int codeSizeEst = decodedByteArray.length < 1000 ? 1000 : decodedByteArray.length / 2;
        byte[] output = new byte[codeSizeEst];

        compresser.setInput(decodedByteArray);
        compresser.finish();

        int compressedDataLength = compresser.deflate(output);
        compresser.end();

        byte gzippedArray[] = new byte[compressedDataLength];
        System.arraycopy(output, 0, gzippedArray, 0, compressedDataLength);
        byte gziped64[] = Base64.encodeBase64(gzippedArray);
        outputString = new String(gziped64, StandardCharsets.UTF_8);
        outputString += ".";
        return outputString;
    }

    /**
     * Dekomprimuje text zakódovaný v kódovaní BASE64.
     *
     * @param str text v kódovaní BASE64
     * @return dekódovaný text
     */
    private static String decompress(String str) throws IOException {
        String outputString = null;
        try {
            String inputString = str.substring(0, str.length() - 1);
            byte[] decodedByteArray = Base64.decodeBase64(inputString.getBytes());
            int delen = decodedByteArray.length * 4;
            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(decodedByteArray);
            byte[] result = new byte[delen];
            int resultLength = decompresser.inflate(result);
            if (resultLength == delen) {
                // musim odhadnut dlzku unzipovanej spravy
                // preto ak je unzipovana dlzka 2x dlhsia tak
                //idem dekodovat este raz a nastavim unzipovanu dlzku na max 10x
                decompresser = new Inflater();
                decompresser.setInput(decodedByteArray);
                result = new byte[delen * 5];
                resultLength = decompresser.inflate(result);
            }
            decompresser.end();

            outputString = new String(result, 0, resultLength);

        } catch (DataFormatException ex) {
            ex.printStackTrace();
            throw new IOException();
        }
        return outputString;
    }

}

