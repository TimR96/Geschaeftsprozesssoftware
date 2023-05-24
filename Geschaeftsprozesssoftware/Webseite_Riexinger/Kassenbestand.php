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
	<a href="Zeiterfassungen.php"><b>Zeige Zeitbuchungen</b></a>	
	<br>
	<br>
	<br>
	<form method="POST" action="Kassenbestand.php">
		<label for="lblKassenbestandAendern"> Kassenbestand ändern:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </label>
		<label for="lblEuro"> Euro: </label>
		<input type="text" name="txtEuro">
		<label for="lblCent"> Cent: </label>
		<input type="text" name="txtCent">
		<input type="submit" name="btnKassenbestandAendern" value="Ändern"/>
	</form>		
	<br>
	<br>

<body style="background-color: rgb(220,255,255);">
<?php
	include 'dbConnector.php';

	if($db->connect_error) {
		die("Verbindung fehlgeschlagen: " . $db->connect_error);		
	}

	$sql = "SELECT Kassenbestand FROM Kassenbestand;";
	
	if(isset($_POST['btnKassenbestandAendern'])) {
	$euro=$_POST['txtEuro'];
	$cent=$_POST['txtCent'];
	$betrag=$euro.'.'.$cent;
	$dmlloeschen = "UPDATE Kassenbestand SET Kassenbestand = $betrag WHERE KassenbestandID = 1";
	mysqli_query($db, $dmlloeschen);
	}

	$ergebnis = $db->query($sql);

	if($ergebnis->num_rows > 0) {
		echo("<table style='width:10%;margin-left: 25px;'>");
		echo("<tr>");
		echo("<th style='border: 2px solid;text-align:left'> Kassenbestand (€) </th>");
		echo("</tr>");		

		while($row = $ergebnis->fetch_assoc()) {
			echo "<tr>";
			echo("<td style='border: 2px solid;text-align:left'> $row[Kassenbestand] </td>");
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