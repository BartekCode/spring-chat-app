'use strict';

// tak odnośimy sie do elementow z html
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');

var privateMessageForm = document.querySelector('#privateMessageForm');
var privateMessageInput = document.querySelector('#privateMessage');
var privateChatArea = document.querySelector('#chat-messages');

var connectingElement = document.querySelector('.connecting');
var logout = document.querySelector('#logout');

var stompClient = null;
var username = null;
var selectedUserId = null;

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

    // display username on logout
    document.querySelector('#connected-user-fullname').textContent = username

    // find and display connected users, wywołanie metody async
    findAndDisplayConnectedUsers().then();

    connectingElement.classList.add('hidden');
}

// tak tworzymy funkcje asynchroniczne w js, korzystamy z tego w requestach http
async function findAndDisplayConnectedUsers() {
    // strzelamy na be zamieniamy odpowiedz na json i mapujemu tespone na json
    let connectedUsers = (await fetch('/users')
        .then(response => response.json()))
        //filtrujemy by w tych userach nie bylo nas
        .filter(user => user.nickName !== username);

    const connectedUsersList = document.querySelector('#connectedUsers');
    connectedUsersList.innerHTML = '';

    connectedUsers.forEach(connectedUser => {
        appendUserElement(connectedUser, connectedUsersList);
        //jezeli jeszcze nie ma ostatniego elementu
        if (connectedUsers.indexOf(connectedUser) < connectedUsers.length - 1) {
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
    listItem.id = user.nickName;

    // tworzymy img
    const userImage = document.createElement('img');
    const userNameText = user.nickName;
    userImage.src = '../img/user_icon.png';
    userImage.alt = userNameText;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = userNameText;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '';
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    // dodajemy do elementu nowe child html elements
    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    listItem.addEventListener('click', userItemClick)

    connectedUsersList.appendChild(listItem);
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach((element) => {
        element.classList.remove('active');
    })

    //ustawiamy private chat na widoczny
    messageArea.classList.add('hidden');
    privateChatArea.classList.remove('hidden');

    messageForm.classList.add('hidden');
    privateMessageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');

    //chat between collectedUser and selected one
    fetchAndDisplayUserChat(selectedUserId).then(); //then zeby wywolac metode async

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden'); // hidden/active czy element html ma sie pokazac czy nie
}

async function fetchAndDisplayUserChat() {
    const userChat = (await fetch(`/messages/${username}/${selectedUserId}`)
        .then(response => response.json()));

    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });

    //display always latest message
    privateChatArea.scrollTop = privateChatArea.scrollHeight;
}

function displayMessage(senderId, message) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('privateMessage');
    if (senderId === username) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const messageText = document.createElement('p');
    messageText.textContent = message;
    //dodajemy utworzony element p do naszego div jako dziecko
    messageContainer.appendChild(messageText);
    privateChatArea.appendChild(messageContainer);
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.send-message", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function sendPrivateMessage(event) {
    const messageContent = privateMessageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            senderId: username,
            receiverId: selectedUserId,
            content: messageContent,
            timestamp: new Date().toISOString()
        };
        stompClient.send("/app/chat.send-private-message", {}, JSON.stringify(chatMessage));
        displayMessage(username, messageContent);
        privateMessageInput.value = '';
    }
    //display always latest message
    privateChatArea.scrollTop = privateChatArea.scrollHeight;

    event.preventDefault();
}

async function onPrivateMessageReceived(payload) {
    await findAndDisplayConnectedUsers().then()
    // message jakie otrzymamy z subskrypcji kanału
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content);
        privateChatArea.scrollTop = privateChatArea.scrollHeight;
    }
    if (selectedUserId) {
        document.querySelector('#${selectedUserId}').classList.add('active');
    } else {
        privateMessageForm.classList.add('hidden')
    }

    const notifiedUser = document.querySelector('#${message.senderId}');
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
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

function onLogout(){
 stompClient.send(
     '/app/user.disconnectUser',
     {},
     JSON.stringify({nickName: username, status: 'OFFLINE'})
    )
    window.location.reload(); // przeladowujemy strone
}

// eventy co ma sie zadziac na wywołaniu danego formulatza
usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
privateMessageForm.addEventListener('submit', sendPrivateMessage, true)
logout.addEventListener('click', onLogout, true)