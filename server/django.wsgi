import os
import sys

path = '/usr/local/apache2/htdocs/django/ryguy'
if path not in sys.path:
	sys.path.append(path)

os.environ['DJANGO_SETTINGS_MODULE'] = 'ryguy.settings'

import django.core.handlers.wsgi

application = django.core.handlers.wsgi.WSGIHandler()


