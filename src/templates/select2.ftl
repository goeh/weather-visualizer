<body>
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
</body>
