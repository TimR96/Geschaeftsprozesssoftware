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
	<a href="Zeiterfassungen.php"><b>Zeige Zeitbuchungen</b></a>
	<a href="Kassenbestand.php"><b>Zeige Kassenbestand</b></a>	
	<br>
	<br>
	<br>
	<form method="POST" action="Abrechnungen.php">
		<label for="lblFiltern"> Einträge filtern - Spalte auswählen:&nbsp;&nbsp;&nbsp;&nbsp; </label>
		<select id="cmbMake" select name="cbxFilter" onchange="document.getElementById('selected_text').value=this.options[this.selectedIndex].text">
			<option>Bezeichnung</option>
			<option>Betrag</option>
			<option>Zeitstempel</option>
			<option>Zahlungsart</option>
			<option>Kategorie</option>
			<option>Mitarbeiter</option>
		</select>
		<label for="lblWertEintragen"> Wert eintragen: </label>
		<input type="text" name="txtFiltern">
		<input type="submit" name="btnFiltern" value="Filtern"/>
	</form>	
	<br>
	<form method="POST" action="Abrechnungen.php">
		<label for="lblSortieren"> Einträge sortieren - Spalte auswählen: </label>
		<select id="cmbMake2" select name="cbxSortieren" onchange="document.getElementById('selected_text').value=this.options[this.selectedIndex].text">
			<option>Bezeichnung</option>
			<option>Betrag</option>
			<option>Zeitstempel</option>
			<option>Zahlungsart</option>
			<option>Kategorie</option>
			<option>Vorname</option>
			<option>Nachname</option>
		</select>
		<select id="cmbMake3" select name="cbxAufAb" onchange="document.getElementById('selected_text').value=this.options[this.selectedIndex].text">
			<option>Aufsteigend</option>
			<option>Absteigend</option>
		</select>
		<input type="submit" name="btnSortieren" value="Sortieren"/>
	</form>	
	<br>
	<form method="POST" action="Abrechnungen.php">
		<label for="lblLoeschen"> Eintrag löschen - Bitte ID eintragen:&nbsp;&nbsp;</label>
		<input type="text" name="txtLoeschen">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="submit" name="btnLoeschen" value="Löschen"/>
	</form>	
	<br>
	<form method="POST" action="Abrechnungen.php">
		<label for="lblWertAendern"> Werte ändern - Bitte ID eintragen:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </label>
		<input type="text" name="txtAendern">
		<label for="lblSpalteWaehlen"> Zu ändernde Spalte wählen </label>
		<select id="cmbMake5" name="cbxSpalten" onchange="document.getElementById('selected_text').value=this.options[this.selectedIndex].text">
			<option>Bezeichnung</option>
			<option>Betrag</option>
		</select>
		<label for="lblWertEintragen"> Neuer Wert: </label>
		<input type="text" name="txtNeuerWert">
		<input type="submit" name="btnWertAendern2" value="Ändern"/>
	</form>
	<br>

<body style="background-color: rgb(220,255,255);">
<?php

	include_once 'dbConnector.php';
	$sql = "SELECT AbrechnungenID, Bezeichnung, Betrag, Zeitstempel, Zahlungsart, Kategorie, Vorname, Nachname FROM Abrechnungen INNER JOIN Zahlungsarten USING (ZahlungsartenID) INNER JOIN Kategorien USING (KategorienID) INNER JOIN Mitarbeiter USING (MitarbeiterID);";


	if($db->connect_error) {
		die("Verbindung fehlgeschlagen: " . $db->connect_error);
	}
	
	if(isset($_POST['btnFiltern'])) {
		$spalte=$_POST['cbxFilter'];
		$wert=$_POST['txtFiltern'];
		if($spalte == "Mitarbeiter") {
			$sql = "SELECT AbrechnungenID, Bezeichnung, Betrag, Zeitstempel, Zahlungsart, Kategorie,
			Vorname, Nachname FROM Abrechnungen INNER JOIN Zahlungsarten USING (ZahlungsartenID) INNER JOIN
			Kategorien USING (KategorienID) INNER JOIN Mitarbeiter USING (MitarbeiterID) WHERE Vorname LIKE
			'%$wert%' OR Nachname LIKE '%$wert%';";
		}
		else {
			$sql = "SELECT AbrechnungenID, Bezeichnung, Betrag, Zeitstempel, Zahlungsart, Kategorie, Vorname,
			Nachname FROM Abrechnungen INNER JOIN Zahlungsarten USING (ZahlungsartenID) INNER JOIN Kategorien
			USING (KategorienID) INNER JOIN Mitarbeiter USING (MitarbeiterID) WHERE $spalte LIKE '%$wert%';";
		}
	}
	
	if(isset($_POST['btnSortieren'])) {
		$spalte=$_POST['cbxSortieren'];
		$abfolge=$_POST['cbxAufAb'];
		if($abfolge == "Aufsteigend")
		{
			$abfolge = "ASC";
		}
		else
		{
			$abfolge = "DESC";
		}
		$sql = "SELECT AbrechnungenID, Bezeichnung, Betrag, Zeitstempel, Zahlungsart, Kategorie, Vorname, Nachname FROM Abrechnungen INNER JOIN Zahlungsarten USING (ZahlungsartenID) INNER JOIN Kategorien USING (KategorienID) INNER JOIN Mitarbeiter USING (MitarbeiterID) ORDER BY $spalte $abfolge;";
	}
	
	if(isset($_POST['btnLoeschen'])) {
		$id=$_POST['txtLoeschen'];
		$dmlLoeschen="DELETE FROM Abrechnungen WHERE AbrechnungenID = $id";
		mysqli_query($db, $dmlLoeschen);
	}
	
	if(isset($_POST['btnWertAendern2'])) {
		$id=$_POST['txtAendern'];
		$spalte=$_POST['cbxSpalten'];
		$wert=$_POST['txtNeuerWert'];
		$dmlAendern = "UPDATE Abrechnungen SET $spalte = '$wert' WHERE AbrechnungenID = $id;";
		mysqli_query($db, $dmlAendern);
	}

	$ergebnis = $db->query($sql);

	if($ergebnis->num_rows > 0) {
		echo("<table style='width:60%;margin-left: 25px;'>");
		echo("<tr>");
		echo("<th style='border: 2px solid;text-align:left'> ID </th>");
		echo("<th style='border: 2px solid;text-align:left'> Bezeichnung </th>");
		echo("<th style='border: 2px solid;text-align:left'> Betrag (€) </th>");
		echo("<th style='border: 2px solid;text-align:left'> Zeitstempel </th>");
		echo("<th style='border: 2px solid;text-align:left'> Zahlungsart </th>");
		echo("<th style='border: 2px solid;text-align:left'> Kategorie </th>");
		echo("<th style='border: 2px solid;text-align:left'> Vorname </th>");
		echo("<th style='border: 2px solid;text-align:left'> Nachname </th>");
		echo("</tr>");		

		while($row = $ergebnis->fetch_assoc()) {
			echo "<tr>";
			echo("<td style='border: 2px solid;text-align:left'> $row[AbrechnungenID] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Bezeichnung] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Betrag] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Zeitstempel] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Zahlungsart] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Kategorie] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Vorname] </td>");
			echo("<td style='border: 2px solid;text-align:left'> $row[Nachname] </td>");
		}
		echo("</tr>");
		echo("</table>");
	}
		else {
			echo "Nichts gefunden";
		}
	$db->close();
?>
</body>
</html>
