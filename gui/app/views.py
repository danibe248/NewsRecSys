from flask import render_template, request, url_for
import json
import socket

from app import app

def json_request(req):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(("127.0.0.1", 22399))
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

@app.route('/u0')
def user0():
	print(url_for('static', filename = 'main.js'))
	req = {}
	req["action"] = "prova"
	req["id"] = 0
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user0.html")

@app.route('/u1')
def user1():
	req = {}
	req["action"] = "prova"
	req["id"] = 1
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user1.html")

@app.route('/u2')
def user2():
	req = {}
	req["action"] = "prova"
	req["id"] = 2
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user2.html")

@app.route('/u3')
def user3():
	req = {}
	req["action"] = "prova"
	req["id"] = 3
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user3.html")

@app.route('/u4')
def user4():
	req = {}
	req["action"] = "prova"
	req["id"] = 4
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user4.html")

@app.route('/u5')
def user5():
	req = {}
	req["action"] = "prova"
	req["id"] = 5
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user5.html")

@app.route('/u6')
def user16():
	req = {}
	req["action"] = "prova"
	req["id"] = 6
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user6.html")

@app.route('/u7')
def user7():
	req = {}
	req["action"] = "prova"
	req["id"] = 7
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user7.html")

@app.route('/u8')
def user8():
	req = {}
	req["action"] = "prova"
	req["id"] = 8
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user8.html")

@app.route('/u9')
def user9():
	req = {}
	req["action"] = "prova"
	req["id"] = 9
	try:
		buffer = json_request(req)
		ack = json.loads(buffer)
		ack["id"] = req["id"]
		return render_template("user0.html",ack=ack)
	except ConnectionRefusedError:
		return render_template("error.html")
	#return render_template("user9.html")

# @app.route('/about')
# def about():
# 	return render_template("about.html")

# @app.route('/boni')
# def boni():
# 	return render_template("boni.html")