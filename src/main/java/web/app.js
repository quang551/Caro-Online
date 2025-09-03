const boardSize = 15;
const boardDiv = document.getElementById("board");
const infoDiv = document.getElementById("info");

let myRole = null;   // "X", "O", hoặc "spectator"
let currentTurn = "X";
let cells = [];

// Tạo bàn cờ
function initBoard() {
    boardDiv.innerHTML = "";
    cells = [];
    for (let i = 0; i < boardSize; i++) {
        cells[i] = [];
        for (let j = 0; j < boardSize; j++) {
            const cell = document.createElement("div");
            cell.className = "cell";
            cell.dataset.x = i;
            cell.dataset.y = j;
            cell.addEventListener("click", onCellClick);
            boardDiv.appendChild(cell);
            cells[i][j] = cell;
        }
    }
}

function onCellClick(e) {
    if (myRole === "spectator") {
        alert("Bạn chỉ được xem, không thể đánh!");
        return;
    }
    const x = e.target.dataset.x;
    const y = e.target.dataset.y;

    // Gửi move lên server
    socket.send(JSON.stringify({ type: "move", x: parseInt(x), y: parseInt(y) }));
}

socket.onmessage = function(event) {
    const data = JSON.parse(event.data);

    if (data.type === "role") {
        alert("Bạn là người chơi: " + data.role);
    }
};
// ================= WebSocket =================
    const socket = new WebSocket("ws://localhost:8080/ws/caro");

    socket.onopen = () => {
        infoDiv.textContent = "Đã kết nối WebSocket!";
    };

    socket.onmessage = (event) => {
        const msg = JSON.parse(event.data);
        console.log("Server:", msg);

        switch (msg.type) {
            case "role":
                myRole = msg.text;
                currentTurn = msg.turn;
                infoDiv.textContent = "Bạn là: " + myRole + " | Lượt hiện tại: " + currentTurn;
                break;

            case "move":
                cells[msg.x][msg.y].textContent = msg.text; // X hoặc O
                currentTurn = msg.turn;
                infoDiv.textContent = "Bạn là: " + myRole + " | Lượt hiện tại: " + currentTurn;
                break;

            case "info":
                infoDiv.textContent = msg.text + " | Lượt: " + msg.turn;
                break;

            case "error":
                alert("❌ Lỗi: " + msg.text);
                break;

            case "win":
                alert("🎉 Người thắng: " + msg.winner);
                initBoard(); // reset lại bàn cờ
                infoDiv.textContent = "Ván mới! Lượt: X";
                break;
        }
    };

    socket.onclose = () => {
        infoDiv.textContent = "❌ Mất kết nối với server!";
    };


// Khởi tạo bàn cờ
    initBoard();

