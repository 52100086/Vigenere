function decryptVigenere(str, key) {
    let decryptedString = "";
    let i_key = 0;

    for (let i = 0; i < str.length; i++) {
        let strChar = str.charAt(i);

        if (!isNaN(strChar)) {
            decryptedString += strChar;
            continue;
        }

        let keyChar = key.charAt(i_key);

        if (strChar === strChar.toUpperCase()) {
            let decryptedChar = (strChar.charCodeAt(0) - keyChar.charCodeAt(0) + 26) % 26 + 65;
            decryptedString += String.fromCharCode(decryptedChar);
            key += String.fromCharCode(decryptedChar);
        } else if (strChar === strChar.toLowerCase()) {
            let decryptedChar = (strChar.charCodeAt(0) - keyChar.charCodeAt(0) + 26) % 26 + 97;
            decryptedString += String.fromCharCode(decryptedChar);
            key += String.fromCharCode(decryptedChar);
        } else {
            decryptedString += strChar;
        }

        i_key++;
    }

    return decryptedString;
}


document.addEventListener("DOMContentLoaded", function() {
    let encryptedInput = document.getElementById("encrypted");
    let decryptedInput = document.getElementById("decrypted");
    let keyInput = document.querySelector(".key");
    let decryptButton = document.querySelector(".btn-decrypt");

    decryptButton.addEventListener("click", function(event) {
        event.preventDefault();

        let encryptedValue = encryptedInput.value;
        let keyValue = keyInput.value;
        let decryptedValue = decryptVigenere(encryptedValue, keyValue);
        decryptedInput.value = decryptedValue;
    });
});




