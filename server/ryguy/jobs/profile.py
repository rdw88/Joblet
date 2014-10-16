'''

All functions in this file MUST return either meaningful data, 
a success flag, or an error code.

'''

from models import Profile
from error import ERROR_NO_SUCH_PROFILE
import base64
import datetime

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
	date_time = datetime.datetime.now()
	encode = '%s%s%s%s' % (args['first_name'], args['last_name'], args['city_code'], date_time)
	profile_id = base64.b64encode(encode, '-_')
	args['profile_id'] = profile_id
	args['date_created'] = date_time
	profile = Profile(first_name=args['first_name'], last_name=args['last_name'], age=args['age'], skills=args['skills'], city_code=args['city_code'], profile_id=profile_id, date_created=date_time)
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

	for key in args:
		prof.__dict__[key] = args[key]

	prof.save()
	return True, None


'''

Completely removes a user's profile from the database.

'''

def delete(args):
	profile = _fetch(args['profile_id'])

	if not profile:
		return None, ERROR_NO_SUCH_PROFILE

	profile[0].delete()
	return True, None


'''

Retrieves a user's profile data from the database given their
unique profile ID.

'''

def get(profile_id):
	profile = _fetch(profile_id)

	if not profile:
		return None, ERROR_NO_SUCH_PROFILE

	vals = profile.values('first_name', 'last_name', 'age', 'city_code', 'skills', 'date_created')
	return str(vals).translate(None, '[]'), None # Maintain proper JSON parse format.


'''

Searches the database given a dictionary of queries to collectively filter through.

'''

def search(args):
	return Profile.objects.filter(args)


'''

Fetches a profile from the database based on its profile_id. Returns None
if the profile id does not match a profile.

'''

def _fetch(profile_id):
	profile = Profile.objects.filter(profile_id=profile_id)

	if len(profile) == 0:
		return None

	return profile