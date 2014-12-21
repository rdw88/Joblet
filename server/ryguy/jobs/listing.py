import base64
import datetime
import re
import profile
import json
import os
from models import Listing, Profile
from error import ERROR_NO_SUCH_LISTING, ERROR_INCORRECT_PASSWORD
from boto.s3.connection import S3Connection
from boto.s3.key import Key
from ryguy.settings import BASE_DIR

'''

Creates a new listing based on the specified paramaters. Creating a new listing
also includes generating a unique listing id. A listing's unique ID is stored
in the creator's profile model so we can reference it in relation to their
profile.

TODO: - Implement job picture functionality.
	  - Hash passwords.

'''

def create(args):
	prof = Profile.objects.get(profile_id=args['profile_id'])

	if prof.password != args['password']:
		return False, ERROR_INCORRECT_PASSWORD

	dt = datetime.datetime.now()
	time_created = dt.strftime('%m-%d-%Y %I:%M%p')
	encode = '%s%s%s' % (args['job_title'], args['job_location'], dt)
	listing_id = base64.b64encode(encode, '-_')
	neg = prof.negative_reputation
	pos = prof.positive_reputation
	rep = (pos / (pos + neg)) * 100 if pos + neg != 0 else 0
	owner_name = '%s %s' % (prof.first_name, prof.last_name)

	listing = Listing(job_title=args['job_title'], job_picture='[]', starting_amount=args['starting_amount'],
		current_bid=args['starting_amount'], min_reputation=args['min_reputation'], job_location=args['job_location'],
		active_time=args['active_time'], owner_name=owner_name, profile_id=args['profile_id'], listing_id=listing_id,
		time_created=time_created, tag=args['tag'], owner_reputation=rep)

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

	vals = listings.values('job_title', 'job_picture', 'starting_amount', 'current_bid', 'min_reputation', 'job_location', 'profile_id', 'time_created', 'is_active', 'owner_reputation', 'owner_name', 'tag')[0]
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
		results = Listing.objects.filter(tag=tag)
		results_list.append(list(results.values('job_title', 'tag', 'owner_reputation', 'current_bid', 'listing_id')))

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

	if operation == 'close' or operation == 'open':
		listing.__dict__['is_active'] = (operation == 'open')
		listing.save()
		return True, None

	elif operation == 'update_current_bid':
		listing.__dict__['current_bid'] = args['current_bid']		
		listing.save()
		return True, None

'''

This edits the private side of the listing. This edits information that only the creator 
of the listing can change. EX: location, minimum reputation, etc...

Edits require the creator's profile_id and password.

'''

def edit(args):
	email = args.pop('email')
	password = args.pop('password')
	prof = Profile.objects.get(email=email)

	if str(prof.password) != password:
		return False, ERROR_INCORRECT_PASSWORD

	listing_id = args.pop('listing_id')
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
	email = args.pop('email')
	password = args.pop('password')
	prof = Profile.objects.get(email=email)

	if prof.password != password:
		return False, ERROR_INCORRECT_PASSWORD

	listing_id = args.pop('listing_id')
	listing = Listing.objects.get(listing_id=listing_id)

	if str(listing.profile_id) != str(prof.profile_id):
		return False, ERROR_INCORRECT_LISTING_OWNERSHIP

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
	name = base64.b64encode(encode, '-_') + '.png'

	listing = Listing.objects.filter(listing_id=listing_id)

	if len(listing) == 0:
		return False, ERROR_NO_SUCH_LISTING

	profile = Profile.objects.get(email=email)

	if profile.password != password:
		return False, ERROR_INCORRECT_PASSWORD

	listing = listing[0]
	if str(listing.profile_id) != str(profile.profile_id):
		return False, ERROR_INCORRECT_LISTING_OWNERSHIP

	file_name = os.path.join(BASE_DIR, 'static/jobs/%s' % name)
	with open(file_name, 'wb') as dest:
		for chunk in uploaded_file.chunks():
			dest.write(chunk)
			dest.flush()

		dest.close()

	conn = S3Connection('AKIAJZTMLPRDUJK6K45A', 'JfKclDtSGZC29w803XnsGq82qRcRKWI+g6TDqmLJ')
	bucket = conn.get_bucket('helpr')
	k = Key(bucket)
	k.key = 'listing/images/%s' % name
	k.set_contents_from_filename(file_name)
	os.remove(file_name)

	pictures = json.loads(listing.__dict__['job_picture'])
	pictures.append(k.key)
	listing.__dict__['job_picture'] = json.dumps(pictures)
	listing.save()

	return True, None