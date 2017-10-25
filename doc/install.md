##Mailtester telepítése

* A war bárhova bemásoltató (legyen például /opt/mailtester)
* A tulajdonos legyen egy nem privilegizált user (legyen például mailtester), az alkalmazás
 ennek a usernek a nevében fog futni
 
 ##Indítás
####service esetén :
* A /etc/init.d ben hozzunk létre egy symlinket pl: <br>
`ln -s /opt/mailtester/mailtester-web-0.1.war /etc/init.d/mailtester`
* service esetén már használható is : <br>
`/etc/init.d/mailtester start|stop|status` 
####systemctl használata esetén :
A mellékelt GIRO_mailtester.service.sample fájlt szerkesszük meg és másoljuk a megfelelő helyre: 
<BR>/etc/systemd/system/GIRO_mailtester.service:<P>
<pre>
[Unit]
Description=myapp
After=syslog.target

[Service]
User=mailtester
ExecStart=/opt/mailtester/mailtester-web-0.1.jar
SuccessExitStatus=143
 
[Install]
WantedBy=multi-user.target
</pre>

##Konfiguráció

Az alkalmazás alapbeállítása szerint a következő portokat használja: <br>
HTTP : 8080<BR>
SMTP : 2525

Ezek a beállítások felülírhatóak ha a war fájl mellett létrehozunk egy azonos nevű
**.conf**   kiterjesztésű állományt, pl. mailtester-web-0.1.conf :<P> 
``JAVA_OPTS="-Dweb.port=8090 -Dsmtp.port=2525"``