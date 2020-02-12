let clientWebSocket = new WebSocket("ws://localhost:8080/event-emitter");
clientWebSocket.onopen = function () {
    console.log("clientWebSocket.onopen", clientWebSocket);
//    clientWebSocket.send("event-me-from-browser");
};
clientWebSocket.onclose = function (error) {
    console.log("clientWebSocket.onclose", clientWebSocket, error);
    events("Closing connection");
};
clientWebSocket.onerror = function (error) {
    console.log("clientWebSocket.onerror", clientWebSocket, error);
    events("An error occurred");
};
clientWebSocket.onmessage = function (error) {
    console.log("clientWebSocket.onmessage", clientWebSocket, error);
    events(error.data);
};

function events(s) {
    let el = document.getElementById('console');
    el.value += "\n" + s;
    el.scrollTop = el.scrollHeight;
}

const node = document.getElementById("msg-input");
node.addEventListener("keyup", sendOnEnter);

function sendOnEnter(event) {
    if (event.key === "Enter") {
        clientWebSocket.send(node.value);
        node.value="";
    }
}
