let currentSessionId = null;


/*
 * Load previous chats
 */

async function loadChats() {

   const response = await fetch("/api/chats");

     const chats = await response.json();

     const history =
         document.getElementById("chat-history");

     history.innerHTML = "";

     chats.forEach(chat => {

         // Container for chat
         const item =
             document.createElement("div");

         item.className = "chat-history-item";


         // Chat title
         const title =
             document.createElement("span");

         title.textContent = chat.title;

         title.className = "chat-title";


         // Delete button
         const deleteButton =
             document.createElement("button");

         deleteButton.textContent = "🗑";

         deleteButton.className =
             "delete-chat-btn";

               // Open chat when title is clicked
                     title.onclick = () => {
                         openChat(chat.id);
                     };


                     // Delete chat
                     deleteButton.onclick = (event) => {

                         event.stopPropagation();

                         deleteChat(chat.id);
                     };


                     item.appendChild(title);

                     item.appendChild(deleteButton);

                     history.appendChild(item);

    });
}


/*
 * Create new chat
 */

async function newChat() {

    const response =
        await fetch(
            "/api/chats",
            {
                method: "POST"
            }
        );

    const chat =
        await response.json();

    currentSessionId =
        chat.id;

    document.getElementById(
        "chat-box"
    ).innerHTML = "";

    document.getElementById(
        "chat-title"
    ).textContent = "New Chat";

    await loadChats();

    document.getElementById(
        "message"
    ).focus();
}


/*
 * Open existing chat
 */

async function openChat(sessionId) {

    currentSessionId =
        sessionId;

    const response =
        await fetch(
            `/api/chats/${sessionId}`
        );

    const messages =
        await response.json();


    const chatBox =
        document.getElementById(
            "chat-box"
        );

    chatBox.innerHTML = "";


    messages.forEach(message => {

        addMessageToUI(
            message.role,
            message.content
        );

    });


    document.getElementById(
        "message"
    ).focus();
}


/*
 * Send message
 */

async function sendMessage() {

    const input =
        document.getElementById(
            "message"
        );

    const message =
        input.value.trim();


    if (!message) {
        return;
    }


    /*
     * Create session automatically
     * if this is the first message
     */

    if (!currentSessionId) {

        await newChat();

    }


    addMessageToUI(
        "USER",
        message
    );


    input.value = "";


    const loading =
        addMessageToUI(
            "ASSISTANT",
            "Thinking..."
        );


    try {

        const response =
            await fetch(
                `/api/chat/${currentSessionId}`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "text/plain"
                    },

                    body: message
                }
            );


        const answer =
            await response.text();


        loading.textContent =
            answer;


        /*
         * Refresh sidebar because
         * chat title may have changed
         */

        await loadChats();


    } catch (error) {

        loading.textContent =
            "Something went wrong.";

        console.error(error);

    }
    scrollToBottom();
}


/*
 * Add message to UI
 */

function addMessageToUI(
    role,
    content
) {

    const chatBox =
        document.getElementById(
            "chat-box"
        );


    const message =
        document.createElement("div");


    if (role === "USER") {

        message.className =
            "message user";

    } else {

        message.className =
            "message bot";
    }


    message.textContent =
        content;


    chatBox.appendChild(
        message
    );


    scrollToBottom();


    return message;
}


/*
 * Enter key
 */

function handleKeyPress(event) {

    if (event.key === "Enter") {

        sendMessage();

    }
}


/*
 * Scroll to bottom
 */

function scrollToBottom() {

    const chatBox =
        document.getElementById(
            "chat-box"
        );

    chatBox.scrollTop =
        chatBox.scrollHeight;
}

async function deleteChat(sessionId) {

    const confirmed =
        confirm("Are you sure you want to delete this chat?");

    if (!confirmed) {
        return;
    }


    try {

        const response = await fetch(
            `/api/chats/${sessionId}`,
            {
                method: "DELETE"
            }
        );


        if (!response.ok) {

            alert("Failed to delete chat");

            return;
        }


        // If currently opened chat was deleted
        if (currentSessionId === sessionId) {

            currentSessionId = null;

            document.getElementById(
                "chat-box"
            ).innerHTML = "";

            document.getElementById(
                "chat-title"
            ).textContent = "AI Assistant";
        }


        // Refresh sidebar
        await loadChats();

    } catch (error) {

        console.error(error);

        alert("Something went wrong while deleting the chat.");
    }
}

/*
 * Load chats when page opens
 */

async function uploadPdf() {

    const fileInput =
        document.getElementById("pdfFile");

    const file = fileInput.files[0];

    if (!file) {
        return;
    }

    if (file.type !== "application/pdf") {

        alert("Please select a PDF file.");

        return;
    }


    const formData = new FormData();

    formData.append("file", file);


    try {

        const response =
            await fetch(
                "/api/documents/upload",
                {
                    method: "POST",
                    body: formData
                }
            );


        if (!response.ok) {

            throw new Error(
                "Upload failed"
            );
        }


        const result =
            await response.text();


        alert(result);


    } catch (error) {

        console.error(error);

        alert(
            "Unable to upload PDF."
        );
    }
    fileInput.value = "";
}

document.addEventListener(
    "DOMContentLoaded",
    loadChats
);
