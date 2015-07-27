scp -r server/ryguy/jobs/ root@ryguy.me:/usr/local/apache2/htdocs/django/ryguy/
scp -r server/ryguy/ryguy/ root@ryguy.me:/usr/local/apache2/htdocs/django/ryguy/
ssh root@ryguy.me '/usr/local/apache2/htdocs/django/ryguy/restart'

