<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="sv" xml:lang="sv">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
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
      <h1>V&auml;derobservationer fr&aring;n Djur&ouml; ${timestamp?string("yyyy-MM-dd HH:mm")}</h1>
	  <table><tbody>
	  	<tr>
	  		<td><img src="temperature.png" title="Den stora nålen visar yttertemperatur och den lilla nålen visar relativ luftfuktighet"/></td>
	  		<td><img src="compass.png" title="Huvudsaklig vindriktning de senaste 10 minuterna"/></td>
	  		<td><img src="wind.png" title="Den stora nålen visar medelhastighet och den lilla nålen högsta uppmätta vindhastighet de senaste 10 minuterna"/></td>
	  	</tr>
	  </tbody></table>
	  
      <p>&nbsp;&quot;${forecast_msg}&quot;</p>

	  <ul>
	    <li>Temperatur: <strong>${temp_out?string("#0.0")}</strong> &deg;C, Upplevs som: <strong>${chill?string("#0.0")}</strong> &deg;C</li>
	    <li>Lufttryck: <strong>${barometer}</strong> millibar och
            <strong>
<#switch bar_trend>
  <#case -60>
     hastigt fallande.
     <#break>
  <#case -20>
     fallande.
     <#break>
  <#case 0>
     stabilt.
     <#break>
  <#case 20>
     stigande.
     <#break>
  <#case 60>
     hastigt stigande.
     <#break>
  <#default>
     ?
</#switch>
            </strong>
            </li>
	    <li>Luftfuktighet: <strong>${hum_out}</strong>%</li>
	    <li>Daggpunkt: <strong>${dew?string("#0.0")} &deg;C</strong></li>
	    <li><a href="anemometer.htm#wind" title="Information om vindhastighet">Vind:</a> <strong title="${wind_dir} grader">${wind_dir_name} ${wind_avg?string("#0.0")}</strong> m/s.
	    Vind i byar <strong>${wind_high?string("#0.0")}</strong> m/s. Dagens h&ouml;gsta vindhastighet: <strong>${dailyHighWindValue?string("#0.0")}</strong> m/s kl. ${dailyHighWindTime?string("HH:mm")}</li>
	    <li><a href="anemometer.htm#rain" title="Information om regnmängd">Nederbörd i dag:</a> <strong>${rain_today?string("#0.0")}</strong> mm</li>
	    <li>Solenergi: <strong>${solar}&nbsp;W/m&sup2;</strong></li>
	    <li><a href="anemometer.htm#uv" title="Information om UV-strålning">UV-index:</a> <strong>${uv?string("#0.0")}</strong></li>
            <li>Solen g&aring;r upp <strong>${sunrise?string("HH:mm")}</strong> och ner <strong>${sunset?string("HH:mm")}</strong>.</li>
           <li>Symboler: <strong>${forecast_icons}</strong></li>
	  </ul>
	  <p>H&auml;r finner Du <a href="history.htm">senaste m&aring;nadens historik</a>.</p>
	  <p>H&auml;r kan du <a href="select.php">sj&auml;lv v&auml;lja</a> vilken period du vill titta p&aring;.</p>
   </div>
  </div>
  <div id="right">
    <div id="navigation">
      <ul>
        <li><a href="../wiki/index.php">Information</a></li>
        <li><a href="../wiki/index.php?title=Nyheter">Nyheter</a></li>
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
