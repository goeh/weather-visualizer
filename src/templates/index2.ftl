<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">

google.load("language", "1");

function initialize() {
google.language.translate("${forecast_msg}", "en", "sv", function(result) {
if (!result.error) {
  var container = document.getElementById("translation");
  container.innerHTML = '"' + result.translation + '"';
}
});
}
google.setOnLoadCallback(initialize);

</script>

<div style="float:right;font-family: arial,helvetica,sans-serif;font-size: 4em; font-weight:bold;color: #3f3333;">${temp_out?string("#0.0")} &deg;C</div>

      <h2>Senaste data fr&aring;n v&auml;derstationen togs emot ${timestamp?string("yyyy-MM-dd HH:mm")}</h2>

      <h3 id="translation" title="&Ouml;vers&auml;ttning gjord med Google Translate">&Ouml;vers&auml;tter engelsk prognos med hj&auml;lp av Google Translate, var god v&auml;nta...</h3>
      <p style="font-size:smaller">&quot;${forecast_msg}&quot;</p>


	  <ul>
	    <li>Temperatur: <strong>${temp_out?string("#0.0")}</strong> &deg;C, upplevs som: <strong>${chill?string("#0.0")}</strong> &deg;C</li>
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
	    <li>Luftfuktighet: <strong>${hum_out}</strong> %</li>
	    <li>Daggpunkt: <strong>${dew?string("#0.0")} &deg;C</strong></li>
	    <li>Vind: <strong title="${wind_dir} grader">${wind_dir_name} ${wind_avg?string("#0.0")}</strong> m/s.
	    Vind i byar <strong>${wind_high?string("#0.0")}</strong> m/s.</li>
	    <li>Solenergi: <strong>${solar}&nbsp;W/m&sup2;</strong></li>
	    <li>UV-index: <strong>${uv?string("#0.0")}</strong>&nbsp;&nbsp;
              <a class="readmore" style="font-size:smaller" href="http://www.stralsakerhetsmyndigheten.se/Allmanhet/Sol-och-solarier/" target="_blank">L&auml;s om UV-str&aring;lning</a></li>
           <li>Symboler: <strong>${forecast_icons}</strong></li>
	  </ul>
          <ul>
            <li>Dagens h&ouml;gsta temperatur: <strong>${dailyHighTempValue?string("#0.0")}</strong> &deg;C kl. ${dailyHighTempTime?string("HH:mm")}</li>
            <li>Dagens l&auml;gsta temperatur: <strong>${dailyLowTempValue?string("#0.0")}</strong> &deg;C kl. ${dailyLowTempTime?string("HH:mm")}</li>
            <li>Dagens h&ouml;gsta vindhastighet: <strong>${dailyHighWindValue?string("#0.0")}</strong> m/s kl. ${dailyHighWindTime?string("HH:mm")}</li>
	    <li>Nederb&ouml;rd i dag: <strong>${rain_today?string("#0.0")}</strong> mm</li>
            <li>Soltimmar senaste 7 dagarna: <strong>${solar_hours?string("#")}</strong> timmar</li>

            <li>
              <span title="T.o.m. ${solligan_time?string("yyyy-MM-dd")}">
                 Soltimmar sedan midsommar: <strong>${solligan?string("#")}</strong> timmar
              </span>
            &nbsp;&nbsp;<a class="readmore" style="font-size:smaller" target="_blank" href="http://www.svt.se/vader/fragor_och_svar/solligan-2015">J&auml;mf&ouml;r med SVT:s &quot;Solligan&quot;</a>
            </li>

            <li>Solen g&aring;r upp <strong>${sunrise?string("HH:mm")}</strong> och ner <strong>${sunset?string("HH:mm")}</strong>.</li>
          </ul>
<!--
	  <p>H&auml;r finner Du <a href="history.htm">senaste m&aring;nadens historik</a>.</p>
	  <p style="text-align: right; margin-bottom: 5px;">
            <a class="slimbutton" href="history.htm">&nbsp;Senaste m&aring;nadens v&auml;derdata&nbsp;</a>
	    <a class="slimbutton" href="select.htm">&nbsp;Historik fr&aring;n 2005 och fram&aring;t&nbsp;</a>
          </p>
-->
