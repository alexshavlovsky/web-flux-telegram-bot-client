const elInput = document.getElementById("msg-input");
const elButton = document.getElementById("btn-submit");
const elConsole = document.getElementById("console");
const elConsoleContainer = document.getElementById("console-container");

elInput.addEventListener("keyup", sendOnEnter);
elButton.addEventListener("click", sendOnButton);

function print(s) {
    let j;
    try {
        j = JSON.parse(s);
    } catch (e) {
        console.log(s);
        return;
    }
    if (j.type === 'log') adaptLog(elConsole, j);
    else if (j.type === 'msg') adaptMsg(elConsole, j);
    elConsoleContainer.scrollTop = elConsoleContainer.scrollHeight;
}

function sendOnButton() {
    if (elInput.value !== "") {
        wsClient.send(elInput.value);
        elInput.value = "";
    }
}

function sendOnEnter(event) {
    if (event.key === "Enter" && elInput.value !== "") {
        wsClient.send(elInput.value);
        elInput.value = "";
    }
}

function url(s) {
    let l = window.location;
    return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + l.pathname + s;
}

let wsClient = new WebSocket(url('messages/' + token));
wsClient.onopen = function () {
};
wsClient.onclose = function () {
//    print("Closing connection");
};
wsClient.onerror = function () {
//    print("An error occurred");
};
wsClient.onmessage = function (event) {
    print(event.data);
};

function adaptLog(cont, ev) {
    let el = document.createElement('p');
    el.classList.add('text-center');
    el.classList.add(ev.payload1 === 'ERROR' ? 'text-danger' : 'text-info');
    el.innerHTML = '<small>' + ev.time + ' ' + ev.message + '</small>';
    cont.appendChild(el);
}

function adaptMsg(cont, ev) {
    let el = document.createElement('p');
    el.classList.add('msg');
    el.classList.add('shad');
    el.classList.add(ev.payload2 === '<' ? 'msg-inc' : 'msg-out');
    el.innerHTML = ev.message;
    cont.appendChild(el);

    el = document.createElement('p');
    el.classList.add(ev.payload2 === '<' ? 'text-left' : 'text-right');
    if (ev.payload2 === '>') el.innerHTML = '<small><span>' + ev.time + '</span><span class="m-2 font-weight-bold">' + ev.payload1 + '</span></small>';
    else el.innerHTML = '<small><span class="m-2 font-weight-bold">' + ev.payload1 + '</span><span>' + ev.time + '</span></small>';
    cont.appendChild(el);
}
