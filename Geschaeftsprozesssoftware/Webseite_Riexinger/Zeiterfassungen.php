<!DOCTYPE html>
<html>
<head>
	<link rel="icon" href="scissors.ico">
	<title>Friseursalon Riexinger </title>
</head>
	<Strong>Friseursalon Riexinger </strong>
	<img src="logo.jpg" align="right"> 
	<br>
	<br>	
	<a href="Abrechnungen.php"><b>Zeige Abrechnungen</b></a>
	<a href="Kassenbestand.php"><b>Zeige Kassenbestand</b></a>	
	<br>
	<br>
	<br>
	<form method="POST" action="Zeiterfassungen.php">
		<label for="lblEintraegeLoeschen"> Einträge filtern - Spalte auswählen:&nbsp;&nbsp; </label>
		<select id="cmbMake" name="cbxFilter"     onchange="document.getElementById('selected_text').value=this.options[this.selectedIndex].text">
			<option>Datum</option>
			<option>Name</option>
		</select>
		<label for="lblWertEintragen"> Wert eintragen: </label>
		<input type="text" name="txtFiltern">
		<input type="submit" name="btnFiltern" value="Filtern"/>
	</form>	
	<br>
	<form method="POST" action="Zeiterfassungen.php">
		<label for="lblLoeschen"> Eintrag löschen - Bitte ID eintragen: </label>
		<input type="text" name="txtLoeschen">
		<input type="submit" name="btnLoeschen" value="löschen">
	</form>
	<br>
	<form method="POST" action="Zeiterfassungen.php">
		<label for="lblWertAendern"> Werte ändern - Bitte ID eintragen:&nbsp;&nbsp;&nbsp;&nbsp; </label>
		<input type="text" name="txtAendern">
		<label for="lblSpalteWaehlen"> Zu ändernde Spalte wählen </label>
		<select id="cmbMake" name="cbxSpalten" onchange="document.getElementById('selected_text').value=this.options[this.selectedIndex].text">
			<option>Datum</option>
			<option>Uhrzeit</option>
			<option>Zeiterfassungsart</option>
		</select>
		<label for="lblWertEintragen"> Neuer Wert: </label>
		<input type="text" name="txtNeuerWert">
		<input type="submit" name="btnWertAendern" value="Ändern"/>
	</form>
<body style="background-color: rgb(220,255,255);">
<br>
<br>
<?php

	include_once 'dbConnector.php';
	$sql = "SELECT ZeiterfassungenID, Datum, Uhrzeit, Vorname, Nachname, Zeiterfassungsart FROM Zeiterfassungen INNER JOIN Mitarbeiter USING (MitarbeiterID) INNER JOIN Zeiterfassungsarten USING (ZeiterfassungsartenID) ORDER BY ZeiterfassungenID;";

	if(isset($_POST['btnLoeschen'])) {
	$var=$_POST['txtLoeschen'];
	$dmlloeschen = "DELETE FROM Zeiterfassungen WHERE ZeiterfassungenID = $var;";
	mysqli_query($db, $dmlloeschen);
	}

	if(isset($_POST['btnFiltern'])) {
		$spalte=$_POST['cbxFilter'];
		$wert=$_POST['txtFiltern'];
		if($spalte == "Datum") {
			$sql = "SELECT ZeiterfassungenID, Datum, Uhrzeit, Vorname, Nachname, Zeiterfassungsart FROM Zeiterfassungen INNER JOIN Mitarbeiter USING (MitarbeiterID) INNER JOIN Zeiterfassungsarten USING (ZeiterfassungsartenID) WHERE Datum LIKE '%$wert%';";
		}
		if($spalte == "Name") {
			$sql = "SELECT ZeiterfassungenID, Datum, Uhrzeit, Vorname, Nachname, Zeiterfassungsart FROM Zeiterfassungen INNER JOIN Mitarbeiter USING (MitarbeiterID) INNER JOIN Zeiterfassungsarten USING (ZeiterfassungsartenID) WHERE Vorname LIKE '%$wert%' OR Nachname LIKE '%$wert%';";
		}		
	}	

	if(isset($_POST['btnWertAendern'])) {
		$AendernID=$_POST['txtAendern'];
		$AenderndeSpalte=$_POST['cbxSpalten'];
		$WertNeu=$_POST['txtNeuerWert'];
		if($AenderndeSpalte == "Zeiterfassungsart") {
			$AenderndeSpalte = "ZeiterfassungsartenID";
			if($WertNeu == "Kommen" OR $WertNeu == "kommen") {
				$WertNeu = 1;
			}
			if($WertNeu == "Gehen" OR $WertNeu == "gehen") {
				$WertNeu = 2;
			}
		}				
		$dmlAendern = "UPDATE Zeiterfassungen SET $AenderndeSpalte = '$WertNeu' WHERE ZeiterfassungenID = $AendernID;";
		mysqli_query($db, $dmlAendern);
	}	

	if($db->connect_error) {
		die("Verbindung fehlgeschlagen: " . $db->connect_error);
			
	}
	
	$ergebnis = $db->query($sql);

	if($ergebnis->num_rows > 0) {
		echo("<table style='width:60%;margin-left: 25px;'>");
		echo("<tr>");
		echo("<th style='border: 2px solid;text-align:left'> ID </th>");
		echo("<th style='border: 2px solid;text-align:left'> Datum </th>");
		echo("<th style='border: 2px solid;text-align:left'> Uhrzeit </th>");
		echo("<th style='border: 2px solid;text-align:left'> Vorname </th>");
		echo("<th style='border: 2px solid;text-align:left'> Nachname </th>");
		echo("<th style='border: 2px solid;text-align:left'> Zeiterfassungsart </th>");
		echo("</tr>");		
		while($row = $ergebnis->fetch_assoc()) {
			echo "<tr>";
			echo("<td style='border: 2px solid;text-align:left'> $row[ZeiterfassungenID] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Datum] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Uhrzeit] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Vorname] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Nachname] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Zeiterfassungsart] </td>");
		}
		echo("</tr>");
		echo("</table>");
	}
		else {
			echo "Nichts gefunden";
		}
	$db->close();
?>
<br>
<br>


</body>
</html>