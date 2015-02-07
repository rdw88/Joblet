import base64
import datetime
import re
import profile
import json
import os
from PIL import Image
from models import Listing, Profile
from error import ERROR_NO_SUCH_LISTING, ERROR_INCORRECT_PASSWORD, ERROR_NO_BID_MADE, ERROR_BID_DOES_NOT_EXIST
from boto.s3.connection import S3Connection
from boto.s3.key import Key
from ryguy.settings import BASE_DIR
from helper import send_push_notification


LISTING_STATUS_ACTIVE = 0
LISTING_STATUS_INACTIVE = 1
LISTING_STATUS_IN_PROGRESS = 2
LISTING_STATUS_COMPLETED = 3


'''

Creates a new listing based on the specified paramaters. Creating a new listing
also includes generating a unique listing id. A listing's unique ID is stored
in the creator's profile model so we can reference it in relation to their
profile.

TODO: - Hash passwords.

'''

def create(args):
	prof = Profile.objects.get(profile_id=args['profile_id'])

	if prof.password != args['password']:
		return False, ERROR_INCORRECT_PASSWORD

	dt = datetime.datetime.now()
	time_created = dt.strftime('%m-%d-%Y %I:%M%p')
	encode = '%s%s%s' % (args['job_title'], args['address'], dt)
	listing_id = base64.b64encode(encode, '-_')
	neg = prof.negative_reputation
	pos = prof.positive_reputation
	rep = (pos / (pos + neg)) * 100 if pos + neg != 0 else 0
	owner_name = '%s %s' % (prof.first_name, prof.last_name)

	listing = Listing(job_title=args['job_title'], job_picture='[]', starting_amount=args['starting_amount'],
		current_bid=args['starting_amount'], min_reputation=args['min_reputation'],
		active_until=args['active_until'], owner_name=owner_name, profile_id=args['profile_id'], listing_id=listing_id,
		time_created=time_created, tag=args['tag'], owner_reputation=rep, status=0, bids='[]', address=args['address'],
		city=args['city'], state=args['state'], lat=args['latitude'], long=args['longitude'])

	listing.save()

	owned_listings = json.loads(prof.owned_listings)
	owned_listings.append(listing_id)
	prof.__dict__['owned_listings'] = json.dumps(owned_listings)
	prof.save()
	
	return { 'error': -1, 'listing_id' : listing_id }, None


'''

This fetches a specific listing based on a given listing ID. If we want
to get a listing based on any of its metadata, we use search().

'''

def get(args):
	listings = Listing.objects.filter(listing_id=args['listing_id'])

	if len(listings) == 0:
		return None, ERROR_NO_SUCH_LISTING

	vals = listings.values('job_title', 'job_picture', 'starting_amount', 'current_bid', 'min_reputation', 'profile_id', 'time_created', 
		'status', 'owner_reputation', 'owner_name', 'tag', 'thumbnail', 'listing_id', 'address', 'city', 'state', 'lat', 'long')[0]
	returned = dict()
	for val in vals:
		returned[val] = vals[val]

	return returned, None


'''

Searches the database for listings that match the specified parameters.
If more than one listing is found, we want to sort the results by the given token.

'''

def search(tokens):
	tags = tokens['tags'].split(',')
	results_list = list()

	for tag in tags:
		results = Listing.objects.filter(tag=tag).filter(status=0)
		results_list.append(list(results.values('job_title', 'tag', 'owner_reputation', 'current_bid', 'listing_id', 'status', 'active_until', 'thumbnail', 'lat', 'long')))

	for i in range(1, len(results_list)):
		for k in range(len(results_list[i])):
			results_list[0].append(results_list[i][k]) 

	return results_list[0], None

	

'''

This updates the public side of the listing. Any user interaction with a specific
listing goes to this function. Updates to a listing can include:

	- Creator opens a job offer to the public.
	- Creator accepts a job offer, therefore closing the listing to the public.
	- Someone offers a lower price to do the job, current price needs to be updated

'''

def update(args):
	operation = args['update_op']
	id = args['listing_id']
	listing = Listing.objects.get(listing_id=id)

	if operation == 'status':
		if int(args['status']) == 2: # Check to make sure at least one bid has been accepted
			if not listing.last_accepted_bid:
				return False, ERROR_NO_BID_MADE



			#send_push_notification()

		listing.__dict__['status'] = args['status']
		listing.save()



		return True, None


'''

This edits the private side of the listing. This edits information that only the creator 
of the listing can change. EX: location, minimum reputation, etc...

Edits require the creator's profile_id and password.

'''

def edit(args):
	email = args['email']
	password = args['password']
	prof = Profile.objects.get(email=email)

	if str(prof.password) != password:
		return False, ERROR_INCORRECT_PASSWORD

	listing_id = args['listing_id']
	listing = Listing.objects.get(listing_id=listing_id)

	if str(listing.profile_id) != str(prof.profile_id):
		return False, ERROR_INCORRECT_LISTING_OWNERSHIP

	for key in args:
		listing.__dict__[key] = args[key]

	listing.save()
	return True, None


'''

Completely removes listing from database.

'''

def delete(args):
	email = args['email']
	password = args['password']
	prof = Profile.objects.get(email=email)

	if prof.password != password:
		return False, ERROR_INCORRECT_PASSWORD

	listing_id = args['listing_id']
	listing = Listing.objects.get(listing_id=listing_id)

	if str(listing.profile_id) != str(prof.profile_id):
		return False, ERROR_INCORRECT_LISTING_OWNERSHIP

	owned_listings = json.loads(prof.owned_listings)
	owned_listings.remove(listing_id)
	prof.__dict__['owned_listings'] = json.dumps(owned_listings)
	prof.save()

	listing.delete()
	return True, None

'''

Uploads pictures associated with a listing.

'''
def upload(args, uploaded_file):
	listing_id = args['listing_id']
	email = args['email']
	password = args['password']
	encode = '%s%s' % (email, datetime.datetime.now())
	name = base64.b64encode(encode, '-_')

	listing = Listing.objects.filter(listing_id=listing_id)

	if len(listing) == 0:
		return False, ERROR_NO_SUCH_LISTING

	profile = Profile.objects.get(email=email)

	if profile.password != password:
		return False, ERROR_INCORRECT_PASSWORD

	listing = listing[0]
	if str(listing.profile_id) != str(profile.profile_id):
		return False, ERROR_INCORRECT_LISTING_OWNERSHIP

	file_name = os.path.join(BASE_DIR, 'static/jobs/%s.png' % name)
	with open(file_name, 'wb') as dest:
		for chunk in uploaded_file.chunks():
			dest.write(chunk)
			dest.flush()

		dest.close()

	picture_url = 'listing/images/%s.png' % name
	thumbnail_url = 'listing/thumbnails/%s.thumbnail' % name
	conn = S3Connection('AKIAI6YGVM3N7FTGKQUA', 'C0wQZ8ov8eeyUVQFOPwcjpJHv7GKyXscJ0QrGF9V')
	bucket = conn.get_bucket('joblet-static')
	
	k = Key(bucket)
	k.key = picture_url
	k.set_contents_from_filename(file_name)
	thumbnail_file = os.path.join(BASE_DIR, 'static/jobs/%s.thumbnail' % name)

	size = 128, 128
	im = Image.open(file_name)
	im.thumbnail(size, Image.ANTIALIAS)
	im.save(thumbnail_file, 'PNG')

	k = Key(bucket)
	k.key = thumbnail_url
	k.set_contents_from_filename(thumbnail_file)

	os.remove(file_name)
	os.remove(thumbnail_file)

	pictures = json.loads(listing.__dict__['job_picture'])
	pictures.append(picture_url)
	listing.__dict__['job_picture'] = json.dumps(pictures)
	listing.__dict__['thumbnail'] = thumbnail_url
	listing.save()

	return True, None


def get_bids(args):	
	listing = Listing.objects.get(listing_id=args['listing_id'])
	return json.loads(listing.bids), None