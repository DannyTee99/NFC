# NFC
 NFC communication between Android mobile and desktop

The project involves NFC communication between an Android Mobile phone and a Desktop application - both programmed in Java. The NFC scanner that is used in this project is the ACR122U. The database that has been used is Firestore. 

#### How it works
You type in the amount of cash you need in the mobile application; hover your phone over the scanner; the desktop application will display the entered number. Please note that your phone needs to have **NFC Compatibilty** for any of this to work.

### The scanner
<div align = "center">
<img src="https://user-images.githubusercontent.com/33988886/90432266-ea3aa300-e0e7-11ea-8689-0dd63c6b6dd2.JPG" width="400" ></div>
<br>
This scanner is compliant with the ISO/IEC18092 standard for Near Field Communication (NFC), it supports not only MIFARE® and ISO 14443 A and B cards, but also all four types of NFC tags.
Drivers for the ACR122u are already available in a Windows computer. If you are not sure , or you use a different computer, the relevant drivers can be downloaded from the following website. <br>
https://www.acs.com.hk/en/driver/3/acr122u-usb-nfc-reader/
<br><br>

### Mobile app and Desktop App
For the mobile application I used android Studio and the Desktop I have used IntelliJ IDE. When importing the projects, pay attention the the errors, if there is any, and download the necesary JAR Files for the applications to work.

### Firestore - Database

Sign up to Firestore and add your project.<br>
https://firebase.google.com/docs/firestore<br>
Go to console and add your project. Follow the instructions and connect your android application and your desktop application to the database.

The documentation of Firestore is very informative if you want to know more. I used firestore instead of firebase because firestore has Java Support. For more information on Firestore:<br> 
https://www.youtube.com/watch?v=v_hR4K4auoQ&list=PLl-K7zZEsYLluG5MCVEzXAQ7ACZBCuZgZ

> There seems to be a problem where the scanner seems to "miss" readings. You need to hover your phone 2-5 times over the scanner for it to be read. I could not find the cause of this in my code and I am not sure if this is a defect in the scanner model. Otherthan that this works fine ~
