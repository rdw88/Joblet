import json
from urllib import urlencode

def send_push_notification(device_id, args):
	headers = {'Content-Type' : 'application/json', 'Authorization' : 'key=AIzaSyCJRsEHM69VixomMuyjLcZ32h7gSp5eAPA' }
	data = {'registration_ids' : [device_id], 'data' : {'data' : urlencode(args)}}
	response = requests.post('https://android.googleapis.com/gcm/send', headers=headers, data=json.dumps(data))