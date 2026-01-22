package Main;

import TicketWallet.HistoryHandler;
import Transactions.TransactionHandler;
import User.*;
import Match.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main (String[] args) throws IOException {

        // create server on 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/matches", new MatchHandler());


        //tell server when js asks for registerHandler, use Regsiter()
        server.createContext("/api/register", new Register());
        // tell server: when js asks for /login, use User.Login()
        server.createContext("/api/login", new Login());
        // tell server: when js asks for Transactions.TransactionHandler use Transactions.Transaction()
        server.createContext("/api/transactions", new TransactionHandler());
        server.createContext("/api/history", new HistoryHandler());


        server.setExecutor(null);
        server.start();


        System.out.println("Server started on port 8080.....");

    }

}