import os
import sys

path = '/usr/local/apache2/htdocs/django/ryguy/'
if path not in sys.path:
	sys.path.append(path)

os.environ['DJANGO_SETTINGS_MODULE'] = 'ryguy.settings'

from django.core.wsgi import get_wsgi_application
application = get_wsgi_application()


