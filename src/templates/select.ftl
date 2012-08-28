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
<title>Djurhamn - Vädret</title>
<link rel="stylesheet" type="text/css" media="screen" href="../screen.css"/>
<link rel="stylesheet" type="text/css" media="print" href="../print.css"/>
<link rel="stylesheet" type="text/css" media="handheld" href="../handheld.css"/>
</head>
<body>
<div id="top">
  <div id="lefthead">
    <h1>Väderhistorik</h1>
    <h2>Latitud N 59`18.41` Longitud O 18`42.08`</h2>
    <h3>Väderhistorik f&ouml;r Djur&ouml; och Vind&ouml;.</h3>
  </div>
  <div id="righthead">
    <p><img src="../images/bridge_small.png" width="140" height="75" alt="Djuröbron"/></p>
  </div>
  <div class="separator"></div>
</div>
<div id="middle">
  <div id="left">
    <div class="box">
      <h1>H&auml;r kan du sj&auml;lv v&auml;lja vilka m&aring;nader du vill se</h1>
      <form method="get">
        <p>Period: <select name="q" onchange="submit()">
<?php
$currentYear = date("Y");
$currentMonth = date("n");
$query = $_REQUEST['q'];
if(!$query) {
  $query = $currentYear;
}
        for($y = $currentYear; $y > 2004; $y--) {
            if($y == $query) {
               $selected = " selected='selected'";
            } else {
               $selected = "";
            }
            echo "<option name='" . $y . "'" . $selected . ">" . $y . "</option>\n";
            if($y == $currentYear) {
                $startMonth = $currentMonth;
            } else {
                $startMonth = 12;
            }
            for($m = $startMonth; $m > 0; $m--) {
              if(strlen($m) == 1) {
                $caption = $y . "-0" . $m;
              } else {
                $caption = $y . "-" . $m;
              }
              /*$caption = $y . "-" . strpad($m, 2, '0', STR_PAD_LEFT);*/
	      if($caption == $query) {
	        $selected = " selected='selected'";
	      } else {
	        $selected = "";
	      }
	      echo "<option name='" . $caption . "'" . $selected . ">" . $caption . "</option>\n";
            }
        }

        echo "</select>&nbsp;&nbsp;&nbsp;&nbsp;<a href='index.htm'>Tillbaka</a></p>\n";

echo "<p><img class='chart' src='temperature_" . $query . ".png'/></p>\n";
echo "<p><img class='chart' src='barometer_" . $query . ".png'/></p>\n";
echo "<p><img class='chart' src='humidity_" . $query . ".png'/></p>\n";
echo "<p><img class='chart' src='wind_" . $query . ".png'/></p>\n";
echo "<p><img class='chart' src='rain_" . $query . ".png'/></p>\n";
echo "<p><img class='chart' src='solar_" . $query . ".png'/></p>\n";
echo "<p><img class='chart' src='uv_" . $query . ".png'/></p>\n";
?>
    </div>
  </div>
  <div id="right">
    <div id="navigation">
      <ul>
        <li><a href="../wiki/index.php">Information</a></li>
        <li><a href="../wiki/index.php?title=Nyheter">Nyheter</a></li>
        <li><a href="../wiki/index.php?title=Evenemang">Evenemang</a></li>
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
