import json
import requests
from urllib import urlencode

PUSH_NOTIFICATION_NEW_BID = 0
PUSH_NOTIFICATION_BID_RESPONSE = 1
PUSH_NOTIFICATION_BID_ACCEPTED = 2
PUSH_NOTIFICATION_BID_FINALIZED = 3

def send_push_notification(device_id, args):
	if device_id == '':
		return

	headers = {'Content-Type' : 'application/json', 'Authorization' : 'key=AIzaSyCJRsEHM69VixomMuyjLcZ32h7gSp5eAPA' }
	data = {'registration_ids' : [device_id], 'data' : {'data' : urlencode(args)}}
	requests.post('https://android.googleapis.com/gcm/send', headers=headers, data=json.dumps(data))