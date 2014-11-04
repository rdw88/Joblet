import base64
import datetime
import re
from profile import check_password
from models import Listing, Profile
from error import ERROR_NO_SUCH_LISTING, ERROR_INCORRECT_PASSWORD

'''

Creates a new listing based on the specified paramaters. Creating a new listing
also includes generating a unique listing id. A listing's unique ID is stored
in the creator's profile model so we can reference it in relation to their
profile.

TODO: - Implement job picture functionality.
	  - Hash passwords.

'''

def create(args):
	current_time = datetime.datetime.now()
	encode = '%s%s%s' % (args['job_title'], args['job_location'], current_time)
	listing_id = base64.b64encode(encode, '-_')

	listing = Listing(job_title=args['job_title'], job_picture='None', starting_amount=args['starting_amount'],
		current_bid=0, min_reputation=args['min_reputation'], job_location=args['job_location'],
		active_time=args['active_time'], profile_id=args['profile_id'], listing_id=listing_id,
		time_created=current_time)

	listing.save()
	return True, None


'''

This fetches a specific listing based on a given listing ID. If we want
to get a listing based on any of its metadata, we use search().

'''

def get(listing_id):
	listings = Listing.objects.filter(listing_id=listing_id)

	if not listings:
		return None, ERROR_NO_SUCH_LISTING

	return str(listings.values('job_title', 'job_picture', 'starting_amount', 'current_bid', 'min_reputation', 'job_location', 'profile_id', 'time_created', 'is_active')).translate(None, '[]'), None


'''

Searches the database for listings that match the specified parameters.
If more than one listing is found, we want to sort the results by the given token.

'''

def search(tokens):
	sort_by = None

	try:
		sort_by = tokens['sort_by']
		del tokens['sort_by']
	except KeyError:
		pass

	d = dict()
	for token in tokens:
		d[token] = tokens[token]

	results = Listing.objects.filter(**d).order_by(sort_by) if sort_by else Listing.objects.filter(**d)
	results_list = [str(result.listing_id) for result in results]

	return results_list, None

	

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