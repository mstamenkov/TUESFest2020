<?php
ini_set('display_errors',1);
session_start();
if(!isset($_SESSION['user_id']))header('Location: ./login.html');
?>

<?php
$id = $_SESSION['user_id'];
require_once "./config.php";
//$stmt = $con->prepare("SELECT * FROM moduleData LEFT JOIN modules m on m.userId = $id WHERE moduleId = m.id AND latitude IS NOT NULL AND longitude IS NOT NULL ;");
$stmt = $con->prepare("SELECT * FROM moduleData t1 LEFT JOIN modules m on m.userId = $id WHERE t1.id = (SELECT t2.id FROM moduleData t2 WHERE t2.moduleId = t1.moduleId ORDER BY t2.id DESC LIMIT 1)AND latitude IS NOT NULL AND longitude IS NOT NULL AND moduleId = m.Id;");
$stmt->execute();
$result = $stmt->get_result();
?>
<!DOCTYPE html>
<html>
<head>
    <title>ГУКТ - Информационна система</title>
    <link rel="icon" href="./textures/icon.png">
    <meta charset="utf-8">
    <link rel="stylesheet" href="style.css" type="text/css">

    <meta http-equiv="content-language" content="en-us, bg" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="viewport" content="initial-scale=1.0">
    <meta charset="utf-8">
    <style>
        /* Always set the map height explicitly to define the size of the div
         * element that contains the map. */
        #map {
            height: 100%;
        }
        /* Optional: Makes the sample page fill the window. */
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
        }
    </style>
</head>
<body>
<div class="header_box">
    <ul>
        <div class="header">
            <img src="./textures/logo.png" alt="logo">
        </div>
        <div id="text">
            <span style="font-weight: bold; margin-bottom: 1%">Lorem Ipsum</span><br>Lorem Ipsum<br>Главно управление по контрол на трафика
        </div>
        <div style="display: inline-flex">
            <span><li><a href ="#" id="active">Начало</a></li></span>
            <span><li><a href ="./devices.php">Модули</a></li></span>
            <span><li><a href ="./events.php">Данни</a></li></span>
            <span><li><a href ="./logout.php">Изход</a></li></span>
        </div>
    </ul>
</div>
<div class="block" style="height: 80%; margin: 1% 2% 0 2%;">
<div id="map"></div>

    <script>
    function initMap() {
        var lat;
        var lng;
        var myLatLng;
        var bounds = new google.maps.LatLngBounds();
        var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 15,
            center: myLatLng
        });
        <?php
        while($list = mysqli_fetch_array($result)){   //Creates a loop to loop through results
                $moduleId = $list['moduleId'];
                $lat = $list['latitude'];
                $lng = $list['longitude'];
                echo "
                lat = $lng;
                lng = $lat;
                myLatLng = {lat,lng};
                var position = new google.maps.LatLng( lat, lng );
                bounds.extend( position );
                var marker = new google.maps.Marker({
                    position: position,
                    map: map,
                    title: '".$moduleId."'
                });";
        }
        ?>
        map.fitBounds(bounds);
    }
</script>
</div>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAwDuh7kjG4WrsgYXnx6dn4d2lKfSvoKLw&callback=initMap"
        async defer></script>
</body>
</html>
