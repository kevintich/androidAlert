#!/usr/bin/env node
var dgram = require("dgram");
var http = require("http");
var URL = require("url");
args = process.argv.splice(2);
var defaults = {'bcast': "192.168.1.255", "bport":'8081', "lport": '8080', "listen":"127.0.0.1"
				"help":function(){
					console.log("ARGUMENTS");
					console.log("\t--help:\tdisplay this help");
					console.log("\t--bcast=ADDR:\tbroadcast address");
					console.log("\t--bport=PORTNO:\tbroadcast port");
					}
				};
Array.prototype.clean = function(clean){
        var i=0;
        for(i=this.length; i>= 0; i--){
                if(this[i]===clean){
                        this.splice(i,1);
                }
        }
        return this;
}
function processArgs(args, defaults){
        var defaults = defaults || {},
        ret = defaults || {};
        reg = /^--([a-z]*)=(.*)$/;
        reg2 = /^--([a-z]*)$/;
        for(var i = 0; i<args.length; i++){
                argument = args[i];
                if(reg.test(argument)){
                        arg = argument.split(reg).clean("");
                        ret[arg[0]] = arg[1];
                }else if(reg2.test(argument)){
                        arg = argument.split(reg2).clean('');
                        if(typeof(defaults[arg[0]]) === "function"){
                                defaults[arg[0]]();
                        }
                }
        }
		return ret;
}
arg = processArgs(args,defaults);
console.log(arg);
var client = dgram.createSocket("udp4");
client.setBroadcast(true);
var server = http.createServer(function(req, res){
	var GET = URL.parse(req.url, true).query;
	res.writeHead(200, {'Content-Type': GET.jsonp?'text\/javascript':'text\/plain'});
	if(GET["send"]){
		var string = JSON.stringify(GET);
		var message = new Buffer(string+"\r\n");
		client.send(message, 0, message.length, 8081, "192.168.1.255");
	}
	if(GET.jsonp)
		res.write(GET.jsonp+'(');
	res.write('{"success": true}');
	if(GET.jsonp)
		res.write(')');
	res.end();
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
