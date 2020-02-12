const elConsole = document.getElementById('console');
const elInput = document.getElementById("msg-input");

elConsole.value = "";
elInput.addEventListener("keyup", sendOnEnter);

function print(s) {
    elConsole.value += s + "\n";
    elConsole.scrollTop = elConsole.scrollHeight;
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
wsClient.onclose = function (error) {
    print("Closing connection");
};
wsClient.onerror = function (error) {
    print("An error occurred");
};
wsClient.onmessage = function (error) {
    print(error.data);
};
