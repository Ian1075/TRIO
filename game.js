document.addEventListener('DOMContentLoaded', fetchGameState);

let aiTurnTimer = null;

async function fetchGameState() {
    try {
        const response = await fetch('/api/game');
        if (!response.ok) throw new Error('Network response was not ok');
        const gameState = await response.json();
        updateUI(gameState);
    } catch (error) {
        console.error('Failed to fetch game state:', error);
        document.body.innerHTML = '<h2>Failed to connect to the game server. Please try again.</h2>';
    }
}

async function performPlayerAction(source, choice) {
    clearTimeout(aiTurnTimer);
    setLoadingState(true, "Processing your move...");
    const url = `/api/player-action?source=${source}&choice=${choice}`;
    try {
        const response = await fetch(url, { method: 'POST' });
        if (!response.ok) throw new Error('Action failed');
        const gameState = await response.json();
        updateUI(gameState);
    } catch (error) {
        console.error('Failed to perform action:', error);
        setLoadingState(false);
        alert("An error occurred. Please try again.");
    }
}

async function performAiStep() {
    const url = `/api/ai-step`;
    try {
        const response = await fetch(url, { method: 'POST' });
        if (!response.ok) throw new Error('AI step failed');
        const gameState = await response.json();
        updateUI(gameState);
    } catch (error) {
        console.error('Failed to perform AI step:', error);
        setLoadingState(false);
    }
}

async function restartGame() {
    // We can't update the UI yet because it doesn't exist.
    // So we first rebuild the basic UI structure.
    rebuildGameUI();
    
    // Now that the UI skeleton is back, we can show a loading message.
    const infoSpan = document.getElementById('current-player-name');
    if(infoSpan) {
        infoSpan.textContent = "Restarting game, please wait...";
    }

    const url = `/api/game/restart`;
    try {
        const response = await fetch(url, { method: 'POST' });
        if (!response.ok) throw new Error('Restart failed');
        
        const newGameState = await response.json();
        
        // The UI skeleton is already there, now we just fill it with data.
        updateUI(newGameState);

    } catch (error) {
        console.error('Failed to restart game:', error);
        alert("Could not restart the game. Please check the console and refresh the page.");
    }
}

function rebuildGameUI() {
    // This function creates the basic skeleton of our game page.
    document.body.innerHTML = `
        <h1>Card Game</h1>
        <div id="game-info">
            <p>Current turn: <span id="current-player-name">Loading...</span></p>
        </div>
        <div id="game-log-container">
            <h3>Last Move</h3>
            <p id="last-move-text">New game started.</p>
        </div>
        <hr>
        <h2>Table Cards</h2>
        <div id="table-cards" class="container"></div>
        <hr>
        <h2>Players</h2>
        <div id="players-container"></div>
    `;
}

function updateUI(gameState) {
    // First, check if the necessary elements exist. If not, it means we need to rebuild.
    // This is a safety check for the restart process.
    if (!document.getElementById('players-container')) {
        rebuildGameUI();
    }
    
    setLoadingState(false);
    clearTimeout(aiTurnTimer);

    if (gameState.winner) {
        const gameOverScreen = document.createElement('div');
        gameOverScreen.id = 'game-over-screen';
        
        const winnerMessage = document.createElement('h1');
        winnerMessage.textContent = `Game Over! Winner is: ${gameState.winner}`;
        
        const restartButton = document.createElement('button');
        restartButton.id = 'restart-button';
        restartButton.textContent = 'Play Again';
        restartButton.onclick = restartGame; // This is where the magic happens
        
        gameOverScreen.appendChild(winnerMessage);
        gameOverScreen.appendChild(restartButton);
        
        document.body.innerHTML = ''; 
        document.body.appendChild(gameOverScreen);
        return;
    }

    // The rest of the updateUI function remains the same...
    const lastMoveElement = document.getElementById('last-move-text');
    if (gameState.lastMoveDescription) {
        lastMoveElement.textContent = gameState.lastMoveDescription;
    }

    document.getElementById('current-player-name').textContent = gameState.players[gameState.currentPlayerIndex].name;
    const isPlayerTurn = gameState.currentPlayerIndex === 0;

    const tableCardsDiv = document.getElementById('table-cards');
    tableCardsDiv.innerHTML = '';
    gameState.tableCards.forEach((card, index) => {
        const cardDiv = createCardDiv(card, 'table', -1, index); 
        tableCardsDiv.appendChild(cardDiv);
    });

    const playersContainer = document.getElementById('players-container');
    playersContainer.innerHTML = '';
    gameState.players.forEach((player, playerIndex) => {
        const playerSection = createPlayerSection(player, playerIndex);
        playersContainer.appendChild(playerSection);
    });

    if (!isPlayerTurn) {
        setLoadingState(true, `Waiting for ${gameState.players[gameState.currentPlayerIndex].name}...`);
        aiTurnTimer = setTimeout(() => {
            performAiStep();
        }, 1200);
    }
}

// in game.js

// in game.js

// in game.js

function createPlayerSection(player, playerIndex) {
    const section = document.createElement('div');
    section.className = 'player-section';
    
    const title = document.createElement('h3');
    title.textContent = player.name;
    section.appendChild(title);
    
    // Container for the visual hand display
    const handDisplayContainer = document.createElement('div');
    handDisplayContainer.className = 'hand-display'; 
    section.appendChild(handDisplayContainer);
    
    if (player.hand.length === 0) {
        handDisplayContainer.textContent = 'No cards in hand.';
        return section;
    }

    if (playerIndex === 0) { // Human player
        section.classList.add('is-human-player');
        
        player.hand.forEach(card => {
            const cardDiv = document.createElement('div');
            cardDiv.className = 'card';
            // Set the background image for the card front
            cardDiv.style.backgroundImage = `url('/images/card-${card.number}.png')`;
            handDisplayContainer.appendChild(cardDiv);
        });

        // Container for action buttons, separate from the hand
        const actionContainer = document.createElement('div');
        actionContainer.className = 'action-buttons-container';
        
        const minCard = player.hand[0];
        const maxCard = player.hand[player.hand.length - 1];
        const minButton = createCardDiv(minCard, 'player-action', playerIndex, 1);
        actionContainer.appendChild(minButton);

        if (player.hand.length > 1) {
            const maxButton = createCardDiv(maxCard, 'player-action', playerIndex, 2);
            actionContainer.appendChild(maxButton);
        }
        section.appendChild(actionContainer);
        
    } else { // AI player
        title.textContent += ` (${player.hand.length} cards)`;
        for(let i = 0; i < player.hand.length; i++) {
            const cardDiv = document.createElement('div');
            cardDiv.className = 'card card-back'; // All AI cards show the back
            // Overlap them slightly for a fanned-out look
            cardDiv.style.marginLeft = (i > 0) ? '-60px' : '0';
            handDisplayContainer.appendChild(cardDiv);
        }
        
        // AI action buttons (optional, could be removed for cleaner look)
        const actionContainer = document.createElement('div');
        actionContainer.className = 'action-buttons-container';
        const minButton = createCardDiv(null, 'ai-action', playerIndex, 1);
        const maxButton = createCardDiv(null, 'ai-action', playerIndex, 2);
        actionContainer.appendChild(minButton);
        if (player.hand.length > 1) {
           actionContainer.appendChild(maxButton);
        }
        section.appendChild(actionContainer);
    }
    return section;
}

// in game.js

function createCardDiv(card, type, source, choice) {
    const div = document.createElement('div');
    div.className = 'card'; // Base class for card look

    switch (type) {
        case 'table':
            div.classList.add('actionable', 'face-down');
            div.onclick = () => performPlayerAction(source, choice);
            break;
        
        case 'player-action':
            // These are now separate buttons, not cards.
            div.className = 'actionable action-button'; // Use new button classes
            if (choice === 1) { // MIN
                div.classList.add('min-button');
                div.textContent = `Flip My MIN`;
            } else { // MAX
                div.classList.add('max-button');
                div.textContent = `Flip My MAX`;
            }
            div.onclick = () => performPlayerAction(source, choice);
            break;
            
        case 'ai-action':
            div.className = 'actionable action-button';
             if (choice === 1) { // MIN
                div.classList.add('min-button');
                div.textContent = `Flip MIN`;
            } else { // MAX
                div.classList.add('max-button');
                div.textContent = `Flip MAX`;
            }
            div.onclick = () => performPlayerAction(source, choice);
            break;
    }
    return div;
}

function setLoadingState(isLoading, message = '') {
    const actionButtons = document.querySelectorAll('.actionable');
    const infoSpan = document.getElementById('current-player-name');
    if (isLoading) {
        actionButtons.forEach(btn => {
            btn.disabled = true;
            btn.style.pointerEvents = 'none';
            btn.style.opacity = '0.5';
        });
        if (message) {
            infoSpan.textContent = message;
        }
    } else {
        actionButtons.forEach(btn => {
            btn.disabled = false;
            btn.style.pointerEvents = 'auto';
            btn.style.opacity = '1';
        });
    }
}