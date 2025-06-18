import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import game.Card;
import game.GameState;
import game.Player;
import game.TrioChecker;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {

    private static GameState gameState = new GameState();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);

        server.createContext("/api/", new GameStateHandler()); 
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started. Open http://localhost:8080 or http://YOUR_IP:8080 in your browser.");
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestedPath = exchange.getRequestURI().getPath();
            
            if (requestedPath.equals("/") || requestedPath.isEmpty()) {
                requestedPath = "/index.html";
            }

            String filePath = "." + requestedPath;
            File file = new File(filePath).getCanonicalFile();

            String currentDir = new File(".").getCanonicalPath();
            if (!file.getCanonicalPath().startsWith(currentDir)) {
                sendErrorResponse(exchange, 403, "403 Forbidden");
                return;
            }

            if (!file.exists() || file.isDirectory()) {
                sendErrorResponse(exchange, 404, "404 Not Found");
            } else {
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody();
                     FileInputStream fs = new FileInputStream(file)) {
                    
                    final byte[] buffer = new byte[1024];
                    int count;
                    while ((count = fs.read(buffer)) > 0) {
                        os.write(buffer, 0, count);
                    }
                }
            }
        }

        private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            exchange.sendResponseHeaders(statusCode, message.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(message.getBytes());
            }
        }
    }

    static class GameStateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String response = "";
            int statusCode = 200;

            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    if ("/api/game".equals(requestPath)) {
                        response = convertGameStateToJson(gameState);
                    } else {
                         statusCode = 404;
                         response = "{\"error\":\"GET endpoint not found: " + requestPath + "\"}";
                    }
                } else if ("POST".equals(exchange.getRequestMethod())) {
                    if ("/api/player-action".equals(requestPath)) {
                        String query = exchange.getRequestURI().getQuery();
                        if (query != null && gameState.winner == null && gameState.getCurrentPlayerIndex() == 0) {
                            int source = Integer.parseInt(getQueryParam(query, "source"));
                            int choice = Integer.parseInt(getQueryParam(query, "choice"));
                            handlePlayerAction(source, choice);
                        }
                    } else if ("/api/ai-step".equals(requestPath)) {
                        if (gameState.winner == null && gameState.getCurrentPlayerIndex() != 0) {
                            handleAiStep();
                        }
                    } else if ("/api/game/restart".equals(requestPath)) {
                        System.out.println("====== GAME RESTARTING ======");
                        gameState = new GameState();
                    } else {
                        statusCode = 404;
                        response = "{\"error\":\"POST endpoint not found: " + requestPath + "\"}";
                    }
                    if(statusCode == 200) {
                        response = convertGameStateToJson(gameState);
                    }
                } else {
                    response = "Unsupported method";
                    statusCode = 405;
                }
            } catch (Exception e) {
                statusCode = 500;
                response = "{\"error\":\"An internal server error occurred.\"}";
                System.err.println("!!!!!!!!!!!!!! SERVER ERROR !!!!!!!!!!!!!!");
                e.printStackTrace();
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void handlePlayerAction(int source, int choice) throws InterruptedException {
            flipCard(source, choice);
            if (gameState.getCurrentFlip().size() == 2) {
                Card c1 = gameState.getCurrentFlip().get(0);
                Card c2 = gameState.getCurrentFlip().get(1);
                if (c1.getNumber() != c2.getNumber()) {
                    endTurn();
                    return;
                }
            }
            if (gameState.getCurrentFlip().size() == 3) {
                checkAndCollectTrio();
                endTurn();
            }
        }

        private void handleAiStep() throws InterruptedException {
            Player aiPlayer = gameState.getCurrentPlayer();
            aiPlayer.setGameState(gameState);
            int[] choice = aiPlayer.chooseCardToFlip();
            flipCard(choice[0], choice[1]);
            if (gameState.getCurrentFlip().size() == 2) {
                Card c1 = gameState.getCurrentFlip().get(0);
                Card c2 = gameState.getCurrentFlip().get(1);
                if (c1.getNumber() != c2.getNumber()) {
                    endTurn();
                    return;
                }
            }
            if (gameState.getCurrentFlip().size() == 3) {
                checkAndCollectTrio();
                endTurn();
            }
        }

        private void checkAndCollectTrio() throws InterruptedException {
            Player currentPlayer = gameState.getCurrentPlayer();
            if (TrioChecker.isBasicTrio(gameState.getCurrentFlip())) {
                
                int trioNumber = gameState.getCurrentFlip().get(0).getNumber();
                String message = currentPlayer.getName() + " collected a TRIO of " + trioNumber + "s!";
                
                System.out.println(message);
                
                gameState.lastMoveDescription = message;
                
                // --- THIS IS THE NEW DELAY LOGIC ---
                // Pause for a moment to celebrate the Trio!
                // We pause for a longer duration for this special event.
                Thread.sleep(1500); // Pause for 1.5 seconds
                // --- END OF NEW LOGIC ---

                java.util.List<Card> collectedTrio = new java.util.ArrayList<>(gameState.getCurrentFlip());
                gameState.getCurrentFlip().clear();
                currentPlayer.collectTrio(collectedTrio);
                if (TrioChecker.hasWinningCondition(currentPlayer.getTrio())) {
                    gameState.winner = currentPlayer;
                    // If a player wins, we can add an even longer pause to build suspense.
                    Thread.sleep(1000); // Extra 1 second pause on win
                }
            }
        }

        private void flipCard(int source, int choice) {
            Card flippedCard = null;
            String actionDescription = ""; 
            Player currentPlayer = gameState.getCurrentPlayer();
            if (source == -1) {
                int originalIndex = choice;
                if (originalIndex >= 0 && originalIndex < gameState.getTableCards().size()) {
                    flippedCard = gameState.getTableCards().get(originalIndex);
                }
            } else {
                Player targetPlayer = gameState.getPlayers().get(source);
                if (choice == 1) { 
                    flippedCard = targetPlayer.getMinCard();
                } else { 
                    flippedCard = targetPlayer.getMaxCard();
                }
            }
            
            if (flippedCard != null) {
                if (source == -1) {
                    gameState.getTableCards().remove(flippedCard);
                } else {
                    gameState.getPlayers().get(source).getHand().remove(flippedCard);
                }
            }

            if (flippedCard != null) {
                if (source == -1) {
                    actionDescription = currentPlayer.getName() + " flipped Table card (Pos " + choice + ") and got a " + flippedCard.getNumber() + ".";
                } else {
                    Player targetPlayer = gameState.getPlayers().get(source);
                    String choiceStr = (choice == 1) ? "MIN" : "MAX";
                    actionDescription = currentPlayer.getName() + " flipped " + targetPlayer.getName() + "'s " + choiceStr + " card and got a " + flippedCard.getNumber() + ".";
                }
                
                gameState.getCurrentFlip().add(flippedCard);
                gameState.addFlippedCard(flippedCard);
                System.out.println(currentPlayer.getName() + " flipped: " + flippedCard);
            } else {
                actionDescription = currentPlayer.getName() + " attempted to flip a card, but failed.";
            }

            gameState.lastMoveDescription = actionDescription;
        }

        private void endTurn() {
            for (Card card : gameState.getCurrentFlip()) {
                int originalSource = card.getSource();
                if (originalSource == -1) {
                    gameState.getTableCards().add(card);
                } else {
                    gameState.getPlayers().get(originalSource).getHand().add(card);
                    gameState.getPlayers().get(originalSource).sortHand();
                }
            }
            gameState.getCurrentFlip().clear();
            gameState.nextPlayer();
        }

        private String getQueryParam(String query, String paramName) {
            if (query == null) return null;
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1 && pair[0].equals(paramName)) {
                    return pair[1];
                }
            }
            return null;
        }
    }
    
    private static String convertGameStateToJson(GameState gs) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (gs.winner != null) {
            json.append("\"winner\": \"").append(gs.winner.getName()).append("\",");
        } else {
            json.append("\"winner\": null,");
        }
        json.append("\"lastMoveDescription\": \"").append(gs.lastMoveDescription.replace("\"", "\\\"")).append("\",");
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