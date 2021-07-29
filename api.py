from flask import Flask, json, request, jsonify, make_response
from flask_sqlalchemy import SQLAlchemy
import uuid
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'

db = SQLAlchemy(app)

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    public_id = db.Column(db.String(50), unique=True)
    vorname = db.Column(db.String(50))
    nachname = db.Column(db.String(50))
    password = db.Column(db.String(100))
    email = db.Column(db.String(100))
    admin = db.Column(db.Boolean)

class Reservierung(db.Model):
    reservierungs_id = db.Column(db.Integer, primary_key=True)
    fahrzeug_id = db.Column(db.Integer)
    public_id = db.Column(db.String(50))
    start = db.Column(db.String)
    ende = db.Column(db.String)
    meter = db.Column(db.Float)

class Fahrt(db.Model):
    fahrt_id = db.Column(db.Integer, primary_key=True)
    reservierungs_id = db.Column(db.Integer, nullable=True)
    fahrzeug_id = db.Column(db.Integer)
    public_id = db.Column(db.String(50))
    start = db.Column(db.String)
    ende = db.Column(db.String)
    meter = db.Column(db.Float)

class Car(db.Model):
    fahrzeug_id = db.Column(db.Integer, primary_key = True)
    marke = db.Column(db.String(20))
    model = db.Column(db.String(20))
    ps = db.Column(db.Integer)
    kilometerstand = db.Column(db.Integer)
    reichweite = db.Column(db.Integer)
    verfügbar = db.Column(db.Boolean)
  

@app.route('/fahrtwithreservation', methods=['POST'])
def create_fahrtwithreservation():
    data = request.get_json()

    new_fahrt = Fahrt(public_id = data['public_id'], fahrzeug_id = data['fahrzeug_id'], start = data['start'], ende = data['ende'], meter = data['meter'], reservierungs_id = data['reservierungs_id'])
    db.session.add(new_fahrt)
    db.session.commit()

    return jsonify({'message' : 'New fahrt created!'})

@app.route('/fahrtwithoutreservation', methods=['POST'])
def create_fahrtwithoutreservation():
    data = request.get_json()

    new_fahrt = Fahrt(public_id = data['public_id'], fahrzeug_id = data['fahrzeug_id'], start = data['start'], ende = data['ende'], meter = data['meter'])
    db.session.add(new_fahrt)
    db.session.commit()

    return jsonify({'message' : 'New fahrt created!'})    

@app.route('/reservierung', methods=['GET'])
def get_all_reservierungen():

    reservierungen = Reservierung.query.all()

    output = []

    for reservierung in reservierungen:
        reservierung_data = {}
        reservierung_data['reservierungs_id'] = reservierung.reservierungs_id
        reservierung_data['fahrzeug_id'] = reservierung.fahrzeug_id
        reservierung_data['public_id'] = reservierung.public_id
        reservierung_data['start'] = reservierung.start
        reservierung_data['ende'] = reservierung.ende
        reservierung_data['meter'] = reservierung.meter

        output.append(reservierung_data)
    return jsonify({'reservierungen' : output})

@app.route('/reservierung/car_id/<reservierungs_id>', methods=['GET'])
def get_car_byreservation(reservierungs_id):

    reservierung = Reservierung.query.filter_by(reservierungs_id=reservierungs_id).first()

    return str(reservierung.fahrzeug_id)

@app.route('/reservierung/<reservierungs_id>', methods=['DELETE'])
def delete_reservierung(reservierungs_id):

    reservierung = Reservierung.query.filter_by(reservierungs_id=reservierungs_id).first()

    if not reservierung:
        return jsonify({'message' : 'No reservierung found!'})

    db.session.delete(reservierung)
    db.session.commit()

    return jsonify({'message' : 'The reservierung has been deleted!'})

@app.route('/onereservierung/<reservierungs_id>', methods=['GET'])
def get_one_reservierung_byid(reservierungs_id):

    reservierung = Reservierung.query.filter_by(reservierungs_id=reservierungs_id).first()
 
    reservierung_data = {}
    reservierung_data['reservierungs_id'] = reservierung.reservierungs_id
    reservierung_data['fahrzeug_id'] = reservierung.fahrzeug_id
    reservierung_data['public_id'] = reservierung.public_id
    reservierung_data['start'] = reservierung.start
    reservierung_data['ende'] = reservierung.ende
    reservierung_data['meter'] = reservierung.meter

    return jsonify(reservierung_data)
        
@app.route('/reservierung/<public_id>', methods=['GET'])
def get_one_reservierung(public_id):
    
    reservierung = Reservierung.query.filter_by(public_id=public_id)

    if not reservierung:
        return jsonify({'message' : 'No reservation Found'})
    output=[]

    for reservierung in reservierung:
        reservierung_data = {}
        reservierung_data['public_id'] = reservierung.public_id
        reservierung_data['fahrzeug_id'] = reservierung.fahrzeug_id
        reservierung_data['reservierungs_id'] = reservierung.reservierungs_id
        reservierung_data['start'] = reservierung.start
        reservierung_data['ende'] = reservierung.ende
        reservierung_data['meter'] = reservierung.meter
        output.append(reservierung_data)
    
    return jsonify({'reservierungen' : output})

@app.route('/reservierung', methods=['POST'])
def create_reservierung():

    data = request.get_json()

    new_reservierung = Reservierung(public_id = data['public_id'], fahrzeug_id = data['fahrzeug_id'], start = data['start'], ende = data['ende'], meter = data['meter'])
    db.session.add(new_reservierung)
    db.session.commit()

    return jsonify({'message' : 'New reservierung created!'})

@app.route('/user', methods=['GET'])
def get_all_users():

    users = User.query.all()

    output = []

    for user in users:
        user_data = {}
        user_data['public_id'] = user.public_id
        user_data['vorname'] = user.vorname
        user_data['nachname'] = user.nachname
        user_data['password'] = user.password
        user_data['email'] = user.email
        user_data['admin'] = user.admin
        output.append(user_data)

    return jsonify({'users' : output})

@app.route('/user/<public_id>', methods=['GET'])
def get_one_user(public_id):

    user = User.query.filter_by(public_id=public_id).first()

    if not user:
        return jsonify({'message' : 'No user found!'})
 
    user_data = {}
    user_data['public_id'] = user.public_id
    user_data['vorname'] = user.vorname
    user_data['nachname'] = user.nachname
    user_data['password'] = user.password
    user_data['email'] = user.email
    user_data['admin'] = user.admin
        
    return jsonify({'user' : user_data})

@app.route('/user', methods=['POST'])
def create_user():

    data = request.get_json()

    hashed_password = generate_password_hash(data['password'], method='sha256')

    new_user = User(public_id=str(uuid.uuid4()), vorname=data['vorname'], nachname=data['nachname'], password=hashed_password, email=data['email'], admin=False)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({'message' : 'New user created!'})

@app.route('/user/<public_id>', methods=['DELETE'])
def delete_user(current_user, public_id):
    if not current_user.admin:
        return jsonify({'message' : 'Cannot perform that function!'})

    user = User.query.filter(public_id==public_id).first()

    if not user:
        return jsonify({'message' : 'No user found!'})

    db.session.delete(user)
    db.session.commit()

    return jsonify({'message' : 'The user has been deleted!'})

@app.route('/car', methods=['GET'])
def get_all_car():

    car = Car.query.all()

    output = []

    for car in car:
        car_data = {}
        car_data['fahrzeug_id'] = car.fahrzeug_id
        car_data['marke'] = car.marke
        car_data['model'] = car.model
        car_data['ps'] = car.ps
        car_data['kilometerstand'] = car.kilometerstand
        car_data['reichweite'] = car.reichweite
        car_data['verfügbar'] = car.verfügbar
        
        output.append(car_data)
    return jsonify({'cars' : output})

@app.route('/car', methods=['POST'])
def create_car():

    data = request.get_json()

    new_car = Car( marke = data['marke'], model = data['model'], ps = data['ps'], kilometerstand = data['kilometerstand'],reichweite = data['reichweite'], verfügbar = data['verfügbar'])
    db.session.add(new_car)
    db.session.commit()

    return jsonify({'message' : 'New Car created!'})

@app.route('/unavailablecar/<fahrzeug_id>', methods=['PUT'])
def makecarunavailable(fahrzeug_id):

    car = Car.query.filter_by(fahrzeug_id=fahrzeug_id).first()

    car.verfügbar = False
    db.session.commit()

    return jsonify({'message' : 'Car is unavailable now'})

@app.route('/availablecar/<fahrzeug_id>', methods=['PUT'])
def makecaravailable(fahrzeug_id):

    car = Car.query.filter_by(fahrzeug_id=fahrzeug_id).first()

    car.verfügbar = True
    db.session.commit()

    return jsonify({'message' : 'Car is unavailable now'})

@app.route('/verfügbarCar', methods=['GET'])
def get_free_car():
    
    car = Car.query.filter_by(verfügbar=True).all()

    
    output=[]

    for car in car:
        car_data = {}
        car_data['fahrzeug_id'] = car.fahrzeug_id
        car_data['marke'] = car.marke
        car_data['model'] = car.model
        car_data['ps'] = car.ps
        car_data['kilometerstand'] = car.kilometerstand
        car_data['reichweite'] = car.reichweite
        car_data['verfügbar'] = car.verfügbar
        output.append(car_data)
    
    return jsonify({'Free Cars' : output})

@app.route('/kilometerstandcar/<fahrzeug_id>', methods=['GET'])
def getKilometerstand(fahrzeug_id):
    car = Car.query.filter_by(fahrzeug_id=fahrzeug_id).first()

    return str(car.kilometerstand)

@app.route('/login')
def login():
    auth = request.authorization

    if not auth or not auth.username or not auth.password:
        return make_response('Could not verify', 401, {'WWW-Authenticate' : 'Basic realm="Login required!"'})

    user = User.query.filter_by(email=auth.username).first()

    if not user:
        return make_response('Could not verify', 401, {'WWW-Authenticate' : 'Basic realm="Login required!"'})

    if check_password_hash(user.password, auth.password):

        print(user.public_id)
        return user.public_id

    return make_response('Could not verify', 401, {'WWW-Authenticate' : 'Basic realm="Login required!"'})



if __name__ == '__main__':
    app.run(debug=True)