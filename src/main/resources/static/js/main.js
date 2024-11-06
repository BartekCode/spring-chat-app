'use strict';

// tak odnośimy sie do elementow z html
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS("/chat-socket");
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError); //callback methods co ma sie zadziac na połaczeniu i błędzie
    }
    event.preventDefault();
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.join",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    //Private messages logic

    // Subscribe to own private channel
    stompClient.subscribe('/user/${username}/queue/messages', onPrivateMessageReceived)

    // register the connected user
    stompClient.send("/app/user.add-user",
        {},
        JSON.stringify({nickName: username, status: 'ONLINE'})
    );

    // find and display connected users, wywołanie metody async
    findAndDisplayConnectedUsers().then();

    connectingElement.classList.add('hidden');
}

// tak tworzymy funkcje asynchroniczne w js, korzystamy z tego w requestach http
async function findAndDisplayConnectedUsers(){
    // strzelamy na be zamieniamy odpowiedz na json i mapujemu tespone na json
    let connectedUsers = (await fetch('/users')
        .then(response => response.json()))
        //filtrujemy by w tych userach nie bylo nas
        .filter(user => user.nickName !== username);

    const connectedUsersList = document.querySelector('#connectedUsers');
    connectedUsersList.innerHTML = '';

    console.log(connectedUsers);

    connectedUsers.forEach(connectedUser => {
       appendUserElement(connectedUser, connectedUsersList);
       //jezeli jeszcze nie ma ostatniego elementu
       if (connectedUsers.indexOf(connectedUser) < connectedUsers.length -1) {
           // add separator, tworzymy element html
           const separator = document.createElement('li');
           // dodajemy classe do naszego html 'separator'
           separator.classList.add('separator');
           // dodajemy child obiekt który utworzylismy
           connectedUsersList.appendChild(separator)
       }
    });

}

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.username;

    // tworzymy img
    const userImage = document.createElement('img');
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.username;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.username;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '';
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    // dodajemy do elementu nowe child html elements
    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    connectedUsersList.appendChild(listItem);
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.send-message", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onPrivateMessageReceived(payload){

}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

// eventy co ma sie zadziac na wywołaniu danego formulatza
usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)