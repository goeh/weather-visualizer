<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="sv" xml:lang="sv">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta name="keywords" lang="se" content="djurhamn,djurö,vindö,björkås,väder,weather,väderprognos,skärgårdsvädret,väderstation,vind,temperatur,regn,nederbörd"/>
<meta name="description" content="Väderobservationer för Djurö och Vindö"/>
<meta name="robots" content="INDEX,FOLLOW"/>
<meta name="resource-type" content="document"/>
<meta name="author" content="Technipelago AB"/>
<meta name="copyright" content="(c) Copyright 2005-2008 Technipelago AB, All rights reserved"/>
<meta name="revisit-after" content="14 days"/>
<meta name="distribution" content="global"/>
<meta name="rating" content="general"/>
<meta http-equiv="Expires" <#setting time_zone="GMT"> content="${expires?string("EEE, d MMM yyyy, HH:mm:ss zzz")}" <#setting time_zone="CET">/>
<meta http-equiv="Cache-Control" content="no-cache, max-age=900, must-revalidate"/>
<title>Djurhamn - Vädret</title>
<link rel="stylesheet" type="text/css" media="screen" href="../screen.css"/>
<link rel="stylesheet" type="text/css" media="print" href="../print.css"/>
<link rel="stylesheet" type="text/css" media="handheld" href="../handheld.css"/>
</head>
<body>
<div id="top">
  <div id="lefthead">
    <h1>Aktuellt väder</h1>
    <h2>Latitud N 59`18.41` Longitud O 18`42.08`</h2>
    <h3>Väderobservationer och prognoser f&ouml;r Djur&ouml; och Vind&ouml;.</h3>
  </div>
  <div id="righthead">
    <p><img src="../images/bridge_small.png" width="140" height="75" alt="Djuröbron"/></p>
  </div>
  <div class="separator"></div>
</div>
<div id="middle">
  <div id="left">
    <div class="box">
      <h1>Väderhistorik från Djurö ${timestamp?string("yyyy-MM-dd HH:mm")}</h1>
	    <p><strong>30 dagars historik</strong>&nbsp;&nbsp;&nbsp;&nbsp;<a href="index.htm">Tillbaka</a></p>
	    <p><img src="temperature_hist.png"/></p>
	    <p><img src="barometer_hist.png"/></p>
	    <p><img src="humidity_hist.png"/></p>
	    <p><img src="wind_hist.png"/></p>
      <p><img src="rain_hist.png"/></p>
	    <p><img src="solar_hist.png"/></p>
	    <p><img src="uv_hist.png"/></p>
	  </div>
  </div>
  <div id="right">
    <div id="navigation">
      <ul>
	    <li><a href="http://www.djurhamn.info/index.htm">Hem</a></li>
        <li><a href="../wiki/index.php">Information</a></li>
        <li><a href="../wiki/index.php?title=Nyheter">Nyheter</a></li>
        <li><a href="../wiki/index.php?title=Evenemang">Evenemang</a></li>
        <li><a href="../wiki/index.php?title=F%C3%B6reningar">Föreningar</a></li>
        <li><a href="../wiki/index.php?title=Samh%C3%A4lle">Samhälle</a></li>
        <li><a href="../wiki/index.php?title=N%C3%A4ringsliv">Näringsliv</a></li>
        <li><a href="../history/index.htm">Historia</a></li>
		<li class="static"><a href="../bredband/index.htm">Bredband</a></li>
        <li class="static"><a href="../fm/index.php">Forum</a></li>
        <li class="current">Väder</li>
        <li class="static"><a href="../kontakt.htm">Kontakt</a></li>
      </ul>
    </div>
    <div id="advert">
      <p><img src="../images/reklam.png" width="140" height="70" alt="Reklam 1"/></p>
      <p><img src="../images/reklam.png" width="140" height="70" alt="Reklam 2"/></p>
      <p><img src="../images/reklam.png" width="140" height="70" alt="Reklam 3"/></p>
      <p><img src="../images/reklam.png" width="140" height="70" alt="Reklam 4"/></p>
    </div>
  </div>
  <div class="separator"></div>
</div>
<div id="foot">
  <p id="davis">Väderdata producerad med en väderstation från Davis Instruments
  	och programvara utvecklad av Technipelago AB.</p>
</div>
</body>
</html>
