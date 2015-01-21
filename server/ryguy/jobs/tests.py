from django.test import TestCase, Client
from models import Profile
import profile
import error

class ProfileTests(TestCase):
	def __init__(self, arg):
		super(ProfileTests, self).__init__(arg)
		self.data = {'email':'ryan@gmail.com', 'first_name':'Ryan', 'last_name':'Wise', 'password':'password', 
		'dob':'1-8-1994', 'tags':'Programming', 'city_code':'San Francisco, CA', 'profile_id':'349384', 
		'date_created': '1-18-2015', 'request':'create'}

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


class ListingTests(TestCase):
	pass