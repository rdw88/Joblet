from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'ryguy.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

#    url(r'^admin/', include(admin.site.urls)),
	url(r'^$', 'music.views.music'),
	url(r'^home/', 'home.views.home'),
	url(r'^profile/', 'jobs.views.profile'),
	url(r'^listing/', 'jobs.views.listings'),
	url(r'^upload/', 'jobs.views.upload'),
	url(r'^bid/', 'jobs.views.bid'),
	url(r'^lolplays/', 'lolplays.views.lolplays'),
	url(r'^spotify-auth', 'music.views.spotify_auth'),
	url(r'^testmusic/', 'newmusic.views.music'),
)
