#!/home/nick/local/node/bin/node
var dgram = require("dgram");
var http = require("http");
var URL = require("url");
var client = dgram.createSocket("udp4");
client.setBroadcast(true);
var server = http.createServer(function(req, res){
	var GET = URL.parse(req.url, true).query;
	res.writeHead(200, {'Content-Type': 'text\/plain'});
	if(GET["send"]){
		var string = JSON.stringify(GET);
		var message = new Buffer(string+"\r\n");
		client.send(message, 0, message.length, 8081, "192.168.1.255");
	}
	res.end('{"success": true}');
});
server.on("close", function(){
	client.close();
	});
server.listen(8080, "127.0.0.1");
process.on("SIGINT", function(){
	console.log("Exiting... buh bye!");
	server.close();
	process.exit();
});
console.log("SYNTAX: http://127.0.0.1:8080/?id=ID&title=TITLE&text=TEXT&send=1");
