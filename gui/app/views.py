from flask import render_template, request, url_for
import json
import socket

from app import app

def json_request(req):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(("127.0.0.1", 21111))
    sock.sendall((json.dumps(req) + "\n").encode("utf8"))
    buffer = b''
    while b'\n' not in buffer:
        data = sock.recv(1024)
        if not data:
            break
        buffer += data
    sock.close()
    buffer = buffer.decode("utf-8")
    return buffer

@app.after_request
def add_header(response):
    response.headers['Cache-Control'] = 'public, max-age=0'
    return response

@app.route('/')
def index():
	return render_template("index.html")

@app.route('/user')
def user0():
	if request.args.get("update") != None:
		req = {}
		req["action"] = "update"
		req["category"] = str(request.args.get("category"))
		req["id"] = int(request.args.get("id"))
		try:
			buffer = json_request(req)
			ack = json.loads(buffer)
			ack["id"] = req["id"]
			ack["cat"] = req["category"]
			return render_template("user0.html",ack=ack)
		except ConnectionRefusedError:
			return render_template("error.html")
	else:
		req = {}
		req["action"] = "recommend"
		req["category"] = "allin"
		req["id"] = int(request.args.get("id"))
		try:
			buffer = json_request(req)
			ack = json.loads(buffer)
			ack["id"] = req["id"]
			ack["cat"] = req["category"]
			return render_template("user0.html",ack=ack)
		except ConnectionRefusedError:
			return render_template("error.html")

@app.route('/user_info')
def user_info():
	req = {}
	req["action"] = "info"
	req["id"] = int(request.args.get("id"))
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("info.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
