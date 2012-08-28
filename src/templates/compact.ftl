<table style="width: 100%; border-collapse: collapse;"><tbody>
<tr><td>Tidpunkt:</td><td>${timestamp?string("yyyy-MM-dd HH:mm")}</td></tr>
<tr><td>Temperatur:</td><td title="Kyleffekt inom parentes"><strong>
${temp_out?string("#0.0")}</strong> &deg;C (${chill?string("#0.0")})</td></tr>
<tr><td>Lufttryck:</td><td>${barometer} hPa
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
</td></tr>
<tr><td>Luftfuktighet:</td><td>${hum_out} %</td></tr>
<tr><td>Vindhastighet:</td><td title="Vind i byar inom parentes">
${wind_dir_name} ${wind_avg?string("#0.0")} m/s (${wind_high?string("#0.0")})</td></tr>
<tr><td>Solenergi:</td><td>${solar}&nbsp;W/m&sup2;, UV ${uv?string("#0.0")}</td></tr>
<tr><td>Soltimmar:</td>
<td>${solar_hours?string("#")} senaste veckan</td></tr>
<tr><td>Nederb&ouml;rd i dag:</td><td>${rain_today?string("#0.0")} mm</td></tr>
</tbody>
</table>
