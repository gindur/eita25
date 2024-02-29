# eita25

#### *Mac*:

*Compile and run server from top directory*
javac -d bin src/server/Server.java src/users/*.java src/util/*.java && java -cp bin server.Server 9876

*Compile and run client from top directory*
javac -d bin src/client/Client.java src/users/*.java src/util/*.java && java -cp bin client.Client localhost 9876


#### *Windows*:

*Compile and run server from top directory*
javac src/client/Client.java src/users/*.java src/util/*.java -d bin; java -cp bin server.Server 9876

*Compile and run client from top directory*
javac src/client/Client.java -d bin; java -cp bin client.Client localhost 9876