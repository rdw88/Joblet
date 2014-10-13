from django.db import models

class Profile(models.Model):
	first_name = models.CharField(max_length=35)
	last_name = models.CharField(max_length=35)
	age = models.IntegerField()
	skills = models.TextField()
	city_code = models.CharField(max_length=16)
	profile_id = models.CharField(max_length=256)
	date_created = models.CharField(max_length=50)

	class Meta:
		verbose_name = 'profile'
		verbose_name_plural = 'profiles'