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
    rebuildGameUI();
    const infoSpan = document.getElementById('current-player-name');
    if(infoSpan) {
        infoSpan.textContent = "Restarting game, please wait...";
    }

    const url = `/api/game/restart`;
    try {
        const response = await fetch(url, { method: 'POST' });
        if (!response.ok) throw new Error('Restart failed');

        const newGameState = await response.json();
        updateUI(newGameState);
    } catch (error) {
        console.error('Failed to restart game:', error);
        alert("Could not restart the game. Please check the console and refresh the page.");
    }
}

function rebuildGameUI() {
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
    console.log('Game state updated:', gameState);
    
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
        restartButton.onclick = restartGame;

        gameOverScreen.appendChild(winnerMessage);
        gameOverScreen.appendChild(restartButton);

        document.body.innerHTML = ''; 
        document.body.appendChild(gameOverScreen);
        return;
    }

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

function createPlayerSection(player, playerIndex) {
    const section = document.createElement('div');
    section.className = 'player-section';

    const title = document.createElement('h3');
    title.textContent = player.name;
    section.appendChild(title);

    const handDisplayContainer = document.createElement('div');
    handDisplayContainer.className = 'hand-display'; 
    section.appendChild(handDisplayContainer);

    if (player.hand.length === 0) {
        handDisplayContainer.textContent = 'No cards in hand.';
        return section;
    }

    if (playerIndex === 0) {
        section.classList.add('is-human-player');

        player.hand.forEach(card => {
            const cardDiv = document.createElement('div');
            cardDiv.className = 'card';
            cardDiv.style.backgroundImage = `url('/images/card-${card.number}.png')`;
            handDisplayContainer.appendChild(cardDiv);
        });

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
    } else {
        title.textContent += ` (${player.hand.length} cards)`;

        const overlapOffset = 90;
        for (let i = 0; i < player.hand.length; i++) {
            const cardDiv = document.createElement('div');
            cardDiv.className = 'card face-down';
            cardDiv.style.position = 'center';
            cardDiv.style.left = `${i * overlapOffset}px`;
            handDisplayContainer.style.width = `${(player.hand.length - 1) * overlapOffset + 150}px`;
            handDisplayContainer.appendChild(cardDiv);
        }

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

    const trios = player.trios || player.trio;
    if (trios && trios.length > 0) {
        const trioContainer = document.createElement('div');
        trioContainer.className = 'trio-display';

        const label = document.createElement('p');
        label.textContent = 'TRIOs Collected:';
        label.style.fontWeight = 'bold';
        label.style.marginBottom = '8px';
        trioContainer.appendChild(label);

        trios.forEach(trio => {
            const trioGroup = document.createElement('div');
            trioGroup.style.display = 'inline-flex';
            trioGroup.style.marginRight = '12px';

            trio.forEach(card => {
                const number = typeof card === 'number' ? card : card.number;
                const cardDiv = document.createElement('div');
                cardDiv.className = 'card';
                cardDiv.style.backgroundImage = `url('/images/card-${number}.png')`;
                trioGroup.appendChild(cardDiv);
            });

            trioContainer.appendChild(trioGroup);
        });

        section.appendChild(trioContainer);
    }

    return section;
}

function createCardDiv(card, type, source, choice) {
    const div = document.createElement('div');
    div.className = 'card';

    switch (type) {
        case 'table':
            div.classList.add('actionable', 'face-down');
            div.onclick = () => performPlayerAction(source, choice);
            break;

        case 'player-action':
            div.className = 'actionable action-button';
            if (choice === 1) {
                div.classList.add('min-button');
                div.textContent = `Flip My MIN`;
            } else {
                div.classList.add('max-button');
                div.textContent = `Flip My MAX`;
            }
            div.onclick = () => performPlayerAction(source, choice);
            break;

        case 'ai-action':
            div.className = 'actionable action-button';
            if (choice === 1) {
                div.classList.add('min-button');
                div.textContent = `Flip MIN`;
            } else {
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
