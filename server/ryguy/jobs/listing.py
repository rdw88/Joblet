import base64
import datetime
from error import ERROR_NO_SUCH_LISTING

'''

Creates a new listing based on the specified paramaters. Creating a new listing
also includes generating a unique listing id. A listing's unique ID is stored
in the creator's profile model so we can reference it in relation to their
profile.

TODO: Implement job picture functionality.

'''

def create(args):
	current_time = datetime.datetime.now()
	encode = '%s%s%s' % (args['job_title'], args['job_location'], current_time)
	listing_id = base64.b64encode(encode, '-_')
	listing = Listing(job_title=args['job_title'], job_picture='None', starting_amount=args['starting_amount'], min_reputation=args['min_reputation'], job_location=args['job_location'], active_time=args['active_time'], profile_id=args['profile_id'], listing_id=listing_id, time_created=current_time)
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

	return str(listings.values('job_title', 'job_picture', 'starting_amount', 'current_bid', 'min_reputation', 'job_location', 'profile_id', 'time_created')).translate(None, '[]')


'''

Searches the database for listings that match the specified parameters.
If more than one listing is found, we want to sort the results by the given token.

'''

def search(tokens):
	pass

'''

This updates the public side of the listing. Any user interaction with a specific
listing goes to this function. Updates to a listing can include:

	- Creator accepts a job offer
	- Someone offers a lower price to do the job, current price needs to be updated

'''

def update(args):
	pass

'''

This edits the private side of the listing. This edits information that only the creator 
of the listing can change. EX: location, minimum reputation, etc...

Edits require the creator's profile_id and password.

'''

def edit(args):
	pass


'''

Completely removes listing from database.

'''

def delete(args):
	pass