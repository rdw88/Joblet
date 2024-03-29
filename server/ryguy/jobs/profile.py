'''

All functions connected to the request field in the POST data in this file
MUST return either meaningful data, a success flag, or an error code.

'''

from models import Profile
from error import ERROR_NO_SUCH_PROFILE, ERROR_INCORRECT_PASSWORD, ERROR_EMAIL_IN_USE
from ryguy.settings import BASE_DIR
import base64
import datetime
import os
import shutil
from boto.s3.connection import S3Connection
from boto.s3.key import Key
from PIL import Image
import notification
import hashlib
import json
import listing as listing_module

'''

Creates a new profile with specified parameters.

Creating a profile involves the following:
	- Storing all given information in the database
	- Generating a unique profile ID and storing this ID with the data in the database.
	- Must check to make sure ID is unique (Might be tough with scalability).

Profile IDs will most likely be a base-64 encoding of the data that the user
has provided. The encoding will also include the data and time the profile was created
to ensure ID uniqueness.

'''

def create(args):
	email = args['email']
	p = Profile.objects.filter(email=email)

	if len(p) > 0:
		return False, ERROR_EMAIL_IN_USE

	date_time = datetime.datetime.now()
	encode = '%s%s%s' % (email, args['city_code'], date_time)
	date_created = '%s-%s-%s' % (date_time.year, date_time.month, date_time.day)
	profile_id = base64.b64encode(encode, '-_')

	password = hash_password(args['password'], profile_id)

	profile = Profile(email=email, first_name=args['first_name'], last_name=args['last_name'], password=password,
		age=args['age'], city_code=args['city_code'], profile_id=profile_id, date_created=date_created, bio=args['bio'])

	profile.save()
	return True, None


'''

Checks if the account exists given an email address and password and
checks to make sure that the password is correct.

'''

def login(args):
	email = args['email']
	password = args['password']
	profile = None

	try:
		profile = Profile.objects.get(email=email)
	except Profile.DoesNotExist:
		return False, ERROR_NO_SUCH_PROFILE

	if not check_password(password, profile):
		return False, ERROR_INCORRECT_PASSWORD

	return True, None


'''

Removes the user's device from the device_id associated with their profile

'''
def logout(args):
	email = args['email']

	try:
		profile = Profile.objects.get(email=email)
	except Profile.DoesNotExist:
		return False, ERROR_NO_SUCH_PROFILE

	profile.__dict__['device_id'] = ''
	profile.save()

	return True, None


'''

Given a user's unique profile ID, changes everything in their
profile that is supplied in args.

'''

def edit(args):
	profile_id = args['profile_id']
	prof = _fetch(profile_id)

	if not prof:
		return None, ERROR_NO_SUCH_PROFILE

	del args['profile_id']
	prof = prof[0]

	if not check_password(args['password'], prof):
		return None, ERROR_INCORRECT_PASSWORD

	del args['password']

	for key in args:
		prof.__dict__[key] = args[key]

	prof.save()
	return True, None


'''

Completely removes a user's profile from the database along with every
listing they own.

'''

def delete(args):
	try:
		profile = Profile.objects.get(email=args['email'])
	except Profile.DoesNotExist:
		return False, ERROR_NO_SUCH_PROFILE

	if not check_password(args['password'], profile):
		return False, ERROR_INCORRECT_PASSWORD

	owned_listings = json.loads(profile.owned_listings)
	for listing_id in owned_listings:
		listing_module.delete({'email': args['email'], 'password': args['password'], 'listing_id': listing_id})

	profile.delete()
	return True, None


'''

Retrieves a user's profile data from the database given their
unique profile ID.

'''

def get(data):
	if 'email' in data:
		profile = Profile.objects.filter(email=data['email'])
	else:
		profile = Profile.objects.filter(profile_id=data['profile_id'])

	if len(profile) != 1:
		return None, ERROR_NO_SUCH_PROFILE

	vals = profile.values('first_name', 'last_name', 'age', 'email', 'city_code', 'tags', 'date_created',
		'positive_reputation', 'negative_reputation', 'jobs_completed', 'listings_completed', 'profile_id', 'owned_listings', 'profile_picture',
		'recent_bids', 'recent_jobs', 'bio')[0]
	vals['date_created'] = vals['date_created'].strftime('%m-%d-%Y')

	return dict(vals), None



def get_notifications(data):
	email = data['email']
	password = data['password']

	try:
		profile = Profile.objects.get(email=email)
	except Profile.DoesNotExist:
		return False, ERROR_NO_SUCH_PROFILE

	if not check_password(password, profile):
		return False, ERROR_INCORRECT_PASSWORD

	return notification.get_all_notifications(profile), None


'''

Uploads a user's profile picture.

'''
def upload(args, uploaded_file):
	email = args['email']
	password = args['password']
	encode = '%s%s' % (email, datetime.datetime.now())
	name = base64.b64encode(encode, '-_')
	profile = None

	try:
		profile = Profile.objects.get(email=email)
	except Profile.DoesNotExist:
		return False, ERROR_NO_SUCH_PROFILE

	if not check_password(password, profile):
		return False, ERROR_INCORRECT_PASSWORD

	file_name = os.path.join(BASE_DIR, 'static/jobs/%s.png' % name) # Temporary File
	with open(file_name, 'wb') as dest:
		for chunk in uploaded_file.chunks():
			dest.write(chunk)
			dest.flush()

		dest.close()

	picture_url = 'profile/pictures/%s.png' % name
	thumbnail_url = 'profile/pictures/thumbnails/%s.png' % name
	conn = S3Connection('AKIAI6YGVM3N7FTGKQUA', 'C0wQZ8ov8eeyUVQFOPwcjpJHv7GKyXscJ0QrGF9V')
	bucket = conn.get_bucket('joblet-static')
	k = Key(bucket)
	k.key = picture_url
	k.set_contents_from_filename(file_name)

	thumbnail_file = os.path.join(BASE_DIR, 'static/jobs/%s.thumbnail' % name)

	size = 255, 255
	im = Image.open(file_name)
	im.thumbnail(size, Image.ANTIALIAS)
	im.save(thumbnail_file, 'PNG')

	k = Key(bucket)
	k.key = thumbnail_url
	k.set_contents_from_filename(thumbnail_file)

	os.remove(file_name)
	os.remove(thumbnail_file)

	profile.__dict__['profile_picture'] = picture_url
	profile.__dict__['profile_picture_thumbnail'] = thumbnail_url
	profile.save()

	return True, None



def check_password(attempt, profile):
	return hash_password(attempt, profile.profile_id) == str(profile.password)


def hash_password(password, profile_id):
	md5 = hashlib.md5()
	salt = '%s%s' % (password, profile_id[:5])
	md5.update(salt)
	return md5.hexdigest()



'''

Fetches a profile from the database based on its profile_id. Returns None
if the profile id does not match a profile.

'''

def _fetch(profile_id):
	profile = Profile.objects.filter(profile_id=profile_id)

	if len(profile) == 0:
		return None

	return profile


def getID(data):
	return { 'profile_id' : Profile.objects.get(email=data['email']).profile_id }, None


def device_id(args):
	profile = Profile.objects.filter(email=args['email'])

	if len(profile) == 0:
		return False, ERROR_NO_SUCH_PROFILE

	profile = profile[0]
	profile.__dict__['device_id'] = args['id']
	profile.save()

	return True, None


'''

Returns true if the provided email can be used to create a new account

'''
def confirm_email(data):
	try:
		Profile.objects.get(email=data['email'])
	except Profile.DoesNotExist:
		return True, None

	return False, ERROR_EMAIL_IN_USE
