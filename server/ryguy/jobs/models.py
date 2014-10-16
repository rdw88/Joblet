from django.db import models

class Profile(models.Model):
	first_name = models.CharField(max_length=32)
	last_name = models.CharField(max_length=32)
	age = models.IntegerField()
	skills = models.TextField()
	city_code = models.CharField(max_length=16)
	profile_id = models.CharField(max_length=256)
	date_created = models.CharField(max_length=64)
	password = models.CharField(max_length=32)

	class Meta:
		verbose_name = 'profile'
		verbose_name_plural = 'profiles'


class Listing(models.Model):
	job_title = models.CharField(max_length=256)
	job_picture = models.CharField(max_length=256)
	starting_amount = models.CharField(max_length=32)
	current_bid = models.CharField(max_length=32)
	min_reputation = models.IntegerField()
	job_location = models.CharField(max_length=256)
	active_time = models.IntegerField() # Stored in minutes.
	profile_id = models.CharField(max_length=256)
	listing_id = models.CharField(max_length=256)
	time_created = models.CharField(max_length=64)

	class Meta:
		verbose_name = 'listing'
		verbose_name_plural = 'listings'