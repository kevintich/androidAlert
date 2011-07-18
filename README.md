# Prerequisites #
1. Android device
2. Node.js

# Set-up #
you'll need a script that monitors for whatever event it is you want a update on, and it'll need to send a request to 127.0.0.1:8080 with the following GET parameters

 * send(Required): can be anything

 * title(optional, but a good idea): the title of the android notification and ticker text

 * text(optional, but a good idea): the body of the android notification

 * id(optional): the ID to use for the android notification, passing the same ID as a previous android notification will just update the old one, if it hasn't been cleared already 
