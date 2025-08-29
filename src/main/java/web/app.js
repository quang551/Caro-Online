const gridSize = 15;
const gridElement = document.getElementById('grid');
const statusEl = document.getElementById('status');
const cells = [];
let currentSymbol = 'X';
let playerName = '';

// Tạo bàn cờ
for (let i = 0; i < gridSize * gridSize; i++) {
    const cell = document.createElement('div');
    cell.classList.add('cell');
    cell.addEventListener('click', () => handleCellClick(i));
    gridElement.appendChild(cell);
    cells.push(cell);
}

function handleCellClick(index) {
    const move = { row: Math.floor(index / 15), col: index % 15 };
    if (window.sendMove) window.sendMove(move);
}

// Nút bắt đầu chơi
document.getElementById('play').addEventListener('click', () => {
    playerName = document.getElementById('playerName').value.trim();
    if (playerName === '') {
        alert('Nhập tên trước khi chơi');
        return;
    }
    document.getElementById('menu').style.display = 'none';
    gridElement.style.display = 'grid';
    statusEl.style.display = 'block';
    statusEl.textContent = `Lượt của ${currentSymbol}`;
});

// Hàm cập nhật bàn cờ từ server
function updateBoard(row, col, symbol) {
    const index = row * 15 + col;
    cells[index].textContent = symbol;
    cells[index].classList.add(symbol);
    currentSymbol = symbol === 'X' ? 'O' : 'X';
    statusEl.textContent = `Lượt của ${currentSymbol}`;
}

// Hàm hiển thị trạng thái từ server
function updateStatus(text) {
    statusEl.textContent = text;
}

// Cho phép Java client gọi
window.updateBoard = updateBoard;
window.updateStatus = updateStatus;
