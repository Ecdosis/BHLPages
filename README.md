About BHLPages
______________

BHLPages is a simple web service runnable as a separate Jetty based 
service on port 8084 or as a Tomcat webapp. Its purpose is to serve 
information about pages on the Biodiversity Heritage Library: their text 
or HTML content or images. It is designed to work with TILT.

Requirements
____________

BHLPages requires installation of aspell and libaspell-dev on Ubuntu, or their
equivalents on other platforms.

You must also install AeseSpeller from
https://github.com/schmidda/AeseSpeller.

For local use you will need to install a web-server. If you use Apache
and mod-proxy you should add the folllowing line to proxy.conf:

ProxyPass /pages/ http://localhost:8084/pages/ retry=0

BHLPages runs on TCP port 8084, so this should be free of other applications
listening on that port.

Installation
____________

    sudo dpkg --install bhpages_1.0-1.deb

This will only work on Ubuntu/Debian systems. 
