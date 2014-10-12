'''

All functions in this file MUST return either meaningful data, 
a success flag, or an error code.

'''


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

def create(first, last, age, skills, city_code):
	pass


'''

Given a user's unique profile ID, changes the flag property 
of their profile with data.

'''

def edit(profile_id, flag, data):
	pass


'''

Completely removes a user's profile from the database.

'''

def delete(profile_id):
	pass


'''

Retrieves a user's profile data from the database given their
unique profile ID.

'''

def get(profile_id):
	pass


'''

Searches the database given a dictionary of queries to collectively filter through.

'''

def search(data):
	pass