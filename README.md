#SendEmail

TASK #1:sending a plain text email to an outside resource, with a disclaimer added at the end, unencrypted and no retry

TASK #2:sending an HTML email to an internal server (so without the disclaimer), encrypted with DES, with the retry functionality

TASK #3:sending an HTML email to an outside resource, with a disclaimer added at the end and encrypted with AES with retries in case of errors

TASK #4:sending a plain text email to an outside resource and encrypted first with DES and then with AES

Below email address is used during implementation. 
For a success scenario, 'EMAIL_TO' parameter should be updated in resources.Configuration.properties file. Once main method is executed, 4 emails (as described above) will be received in recipients inbox.
_Note:_ AES and DES encryption require cipher private keys. A random string can be given. However, since we do not have control on SMTP server, the received mail will not be decrypted. 'security/Encrypt' class contains implementation both for AES and DES. However, due to mentioned reason, I have not used. Instead, in interface, there is a default 'secure' method, which is to give opportunity to each service implementation to implement required encryption algorithm. As of now, I have placed custom informative message. 

For retry option, 'EMAIL_TO' parameter should be set to an invalid email address. In such case, the sender receives an email. 'receiveChannel' bean initiates the channel which keeps checking sender's inbox and notifies when such message is received. 


Requirement: 
    Java 1.8+,
    Maven 3.2.5+

luxoftintechtask@gmail.com
luxoftintechtask/luxoft1234

Backup Mail
luxofttask@gmail.com
luxofttask/luxoft1234