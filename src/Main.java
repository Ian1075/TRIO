// Main.java
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import game.Card;
import game.GameState;
import game.Player;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {
    private static GameState gameState = new GameState();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);

        server.createContext("/api/game", new GameStateHandler());
        server.createContext("/", new StaticFileHandler());

        server.start();
        System.out.println("Server started. Open http://localhost:8080 in your browser.");
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("." + path).getCanonicalFile();

            if (!file.exists()) {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(200, file.length());
                OutputStream os = exchange.getResponseBody();
                FileInputStream fs = new FileInputStream(file);
                final byte[] buffer = new byte[1024];
                int count;
                while ((count = fs.read(buffer)) > 0) {
                    os.write(buffer, 0, count);
                }
                fs.close();
                os.close();
            }
        }
    }

    static class GameStateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            if ("GET".equals(exchange.getRequestMethod())) {
                response = convertGameStateToJson(gameState);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else {
                response = "Unsupported method";
                exchange.sendResponseHeaders(405, response.length());
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    private static String convertGameStateToJson(GameState gs) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"currentPlayerIndex\": ").append(gs.getCurrentPlayerIndex()).append(",");
        
        json.append("\"players\": [");
        for (int i = 0; i < gs.getPlayers().size(); i++) {
            Player p = gs.getPlayers().get(i);
            json.append("{");
            json.append("\"name\": \"").append(p.getName()).append("\",");
            json.append("\"hand\": [");
            for (int j = 0; j < p.getHand().size(); j++) {
                Card c = p.getHand().get(j);
                json.append("{\"number\": ").append(c.getNumber()).append(", \"id\": ").append(c.getId()).append("}");
                if (j < p.getHand().size() - 1) json.append(",");
            }
            json.append("]}");
            if (i < gs.getPlayers().size() - 1) json.append(",");
        }
        json.append("],");
        
        json.append("\"tableCards\": [");
        for (int i = 0; i < gs.getTableCards().size(); i++) {
            Card c = gs.getTableCards().get(i);
            json.append("{\"number\": ").append(c.getNumber()).append(", \"id\": ").append(c.getId()).append("}");
            if (i < gs.getTableCards().size() - 1) json.append(",");
        }
        json.append("]");

        json.append("}");
        return json.toString();
    }
}