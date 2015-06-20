from django.core import serializers

import json
import requests
import base64
import time
import datetime
from urllib import urlencode
from models import Profile, Notification


PUSH_NOTIFICATION_NEW_BID = 0
PUSH_NOTIFICATION_BID_RESPONSE = 1
PUSH_NOTIFICATION_BID_ACCEPTED = 2
PUSH_NOTIFICATION_BID_FINALIZED = 3


def create(title, description, email, password, extras):
	try:
		profile = Profile.objects.get(email=email)
	except Profile.DoesNotExist:
		return False

	if password != profile.password:
		return False

	encode = '%s%s' % (int(round(time.time() * 1000)), email)
	notification_id = base64.b64encode(encode, '-_')
	now = datetime.datetime.now()
	dt = now.strftime('%m/%d/%Y %I:%M%p %z')

	notification = Notification(notification_id=notification_id, title=title, description=description, email=email, password=password, time_created=dt)
	notification.save()

	device_id = str(profile.device_id)
	send_push_notification(device_id, title, description, extras)

	return True



def delete(notification_id):
	try:
		notification = Notification.objects.get(notification_id=notification_id)
	except Notification.DoesNotExist:
		return False

	notification.delete()
	return True



def get_all_notifications(profile):
	notifications = Notification.objects.filter(email=profile.email)
	return list(notifications.values())



def send_push_notification(device_id, title, description, extras):
	if device_id == '':
		return

	data = {'title' : title, 'description': description}
	if extras:
		data['extras'] = json.dumps(extras)

	headers = {'Content-Type' : 'application/json', 'Authorization' : 'key=AIzaSyCJRsEHM69VixomMuyjLcZ32h7gSp5eAPA' }
	data = {'registration_ids' : [device_id], 'data': data}
	requests.post('https://android.googleapis.com/gcm/send', headers=headers, data=json.dumps(data))