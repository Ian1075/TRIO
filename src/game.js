
document.addEventListener('DOMContentLoaded', fetchGameState);

async function fetchGameState() {
    try {
        const response = await fetch('/api/game');
        if (!response.ok) throw new Error('Network response was not ok');
        const gameState = await response.json();
        updateUI(gameState);
    } catch (error) {
        console.error('Failed to fetch game state:', error);
    }
}

function updateUI(gameState) {
    document.getElementById('current-player-name').textContent = gameState.players[gameState.currentPlayerIndex].name;

    const player0Hand = document.getElementById('player0-hand');
    const otherPlayersDiv = document.getElementById('other-players');
    const tableCardsDiv = document.getElementById('table-cards');
    player0Hand.innerHTML = '';
    otherPlayersDiv.innerHTML = '';
    tableCardsDiv.innerHTML = '';

    gameState.players.forEach((player, index) => {
        if (index === 0) {
            player.hand.forEach(card => {
                const cardDiv = createCardDiv(card, 'hand', index);
                player0Hand.appendChild(cardDiv);
            });
        } else {
            const playerDiv = document.createElement('div');
            playerDiv.innerHTML = `<b>${player.name}</b> has ${player.hand.length} cards.`;
            otherPlayersDiv.appendChild(playerDiv);
        }
    });

    gameState.tableCards.forEach((card, index) => {
        const cardDiv = createCardDiv(card, 'table', index);
        tableCardsDiv.appendChild(cardDiv);
    });
}

function createCardDiv(card, type, index) {
    const div = document.createElement('div');
    if (type === 'hand') {
        div.className = 'card';
        div.textContent = `Card #${card.number}`;
    } else {
        div.className = 'card face-down';
        div.textContent = `Table Card ${index}`;
        div.onclick = () => {
            alert(`You clicked table card at index ${index}!`);
        };
    }
    return div;
}