const elInput = document.getElementById("msg-input");
const elConsole = document.getElementById("console");
const elConsoleContainer = document.getElementById("console-container");

elInput.addEventListener("keyup", sendOnEnter);

function print(s) {
    let msg = document.createElement('div');
    msg.classList.add('alert');
    msg.classList.add('alert-primary');
    msg.setAttribute("role", "alert");
    msg.innerHTML = s;
    elConsole.appendChild(msg);
    elConsoleContainer.scrollTop = elConsoleContainer.scrollHeight;
}

function sendOnEnter(event) {
    if (event.key === "Enter") {
        wsClient.send(elInput.value);
        elInput.value = "";
    }
}

let wsClient = new WebSocket("ws://localhost:8080/messages/" + token);
wsClient.onopen = function () {
};
wsClient.onclose = function () {
    print("Closing connection");
};
wsClient.onerror = function () {
    print("An error occurred");
};
wsClient.onmessage = function (event) {
    print(event.data);
};
