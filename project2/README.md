# eita25

#### *Mac*:

*Compile and run server from top directory*
javac -d bin src/server/Server.java && java -cp bin src.server.Server 9876

*Compile and run client from top directory*
javac -d bin src/client/Client.java && java -cp bin src.client.Client localhost 9876


#### *Windows*:

*Compile and run server from top directory*
javac src/server/Server.java -d bin; java -cp bin server.Server 9876

*Compile and run client from top directory*
javac src/client/Client.java -d bin; java -cp bin src.client.Client localhost 9876