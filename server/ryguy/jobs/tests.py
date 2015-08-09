from django.test import TestCase, Client
from models import Profile, Listing
import profile
import listing
import error
import time
import datetime

class ProfileTests(TestCase):
	def __init__(self, arg):
		super(ProfileTests, self).__init__(arg)
		self.data = {'email':'ryan@gmail.com', 'first_name':'Ryan', 'last_name':'Wise', 'password':'password', 
		'age': 21, 'tags':'Programming', 'city_code':'San Francisco, CA',
		'request':'create', 'bio': 'This is my random bio.'}

	def test_profile_creation(self):
		res, err = profile.create(self.data)
		self.assertEqual(res, True)

		res, err = profile.create(self.data)		
		self.assertEqual(err, error.ERROR_EMAIL_IN_USE)

	def test_profile_login(self):
		data = self.data
		profile.create(data)
		res, err = profile.login(data)
		self.assertEqual(res, True)

		data['password'] = '1234'
		res, err = profile.login(data)
		self.assertEqual(err, error.ERROR_INCORRECT_PASSWORD)

		data['email'] = '29389@gmail.com'
		res, err = profile.login(data)
		self.assertEqual(err, error.ERROR_NO_SUCH_PROFILE)


	def test_get_profile(self):
		profile_id = self._get_profile_id()
		self.data['profile_id'] = profile_id

		fake_profile, err_code = profile.get({'profile_id' : 'fjefk'})
		self.assertEqual(err_code, error.ERROR_NO_SUCH_PROFILE)

		fetched_profile, err_code = profile.get({'profile_id' : profile_id})
		self.assertEqual(err_code, None)

		for val in fetched_profile:
			if val in self.data:
				self.assertEqual(str(self.data[val]), str(fetched_profile[val]))


	def test_edit_profile(self):
		NEW_CITY = 'Eugene, OR'

		profile_id = self._get_profile_id()
		args = {'profile_id' : '34', 'password': '3499'} # Does not exist
		res, err = profile.edit(args)

		self.assertEqual(err, error.ERROR_NO_SUCH_PROFILE)
		args['profile_id'] = profile_id # Still contains incorrect password
		res, err = profile.edit(args)
		self.assertEqual(err, error.ERROR_INCORRECT_PASSWORD)

		args['password'] = self.data['password']
		args['city_code'] = NEW_CITY
		args['profile_id'] = profile_id
		res, err = profile.edit(args)

		self.assertEqual(err, None)
		edited_profile, err = profile.get({'profile_id' : profile_id})

		self.assertEqual(edited_profile['city_code'], NEW_CITY)


	def _get_profile_id(self):
		res, err = profile.create(self.data)
		self.assertEqual(err, None)
		return profile.getID({'email' : self.data['email']})[0]['profile_id']



class ListingTests(TestCase):
	def __init__(self, arg):
		super(ListingTests, self).__init__(arg)
		self.test_profile = {'email':'ryan@gmail.com', 'first_name':'Ryan', 'last_name':'Wise', 'password':'password', 
		'age': 21, 'tags':'Programming', 'city_code':'San Francisco, CA',
		'request':'create', 'bio': 'This is my random bio.'}

		self.test_listing = {'password':'password',
		'job_title' : 'Random job', 'starting_amount' : 50, 'address' : '1234 h ln', 'city':'Oakland',
		'state':'CA', 'latitude' : 45, 'longitude': 55,
		'owner_email': 'ryan@gmail.com'}

	def listing_creation(self):
		prof_id = Profile.objects.get(email=self.test_profile['email']).profile_id
		self.test_listing['profile_id'] = prof_id
		response, error = listing.create(self.test_listing)	

		self.assertEqual(error, None)

		self.listing_id = response['listing_id']


	def get_listing(self):
		response, error = listing.get({'listing_id' : self.listing_id})
		self.assertEqual(error, None)
		
		for key in response:
			if key in self.test_listing:
				self.assertEqual(self.test_listing[key], response[key])


	def profile_deletion_with_listings(self):
		profile.delete({'email': self.test_profile['email'], 'password': self.test_profile['password']})
		profiles = Profile.objects.filter(email=self.test_profile['email'])
		listings = Listing.objects.filter(owner_email=self.test_profile['email'])
		self.assertEqual(len(listings), 0)
		self.assertEqual(len(profiles), 0)


	def test_start(self):
		profile.create(self.test_profile)
		self.listing_creation()
		self.get_listing()
		self.listing_creation()
		self.profile_deletion_with_listings()