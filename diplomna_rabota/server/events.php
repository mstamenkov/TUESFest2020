<?php
session_start();
if(empty($_GET['apicall'])){
    require_once "./config.php";
    if (!isset($_SESSION['user_id'])) header('Location: ./login.html');
    $id = $_SESSION['user_id'];
    $stmt = $con->prepare("SELECT * FROM modules WHERE userId = ?");
    $stmt->bind_param('i', $id);
    $stmt->execute();
    $modulesIdArray = $stmt->get_result();
}else{
    require_once "./config.php";
    $username = $_GET['username'];
    $stmt = $con->prepare("SELECT id FROM users WHERE username = ?");
    $stmt->bind_param('i', $username);
    $stmt->execute();
    $result = $stmt->get_result();
}

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
            <span><li><a href ="index.php">Начало</a></li></span>
            <span><li><a href ="./devices.php">Модули</a></li></span>
            <span><li><a href ="./events.php" id="active">Данни</a></li></span>
            <span><li><a href ="./logout.php">Изход</a></li></span>
        </div>
    </ul>
</div>
<div class='block'>
    <label for="moduleId">Налични модули:</label>
    <form action='events.php' method='POST'>
    <select name="moduleId">
        <?php
        $isModuleOwnedBy = 0;
        $showData = 0;
        $moduleId = filter_input(INPUT_POST,'moduleId');

        if(!empty($moduleId))$showData = 1;
        while($res = mysqli_fetch_array($modulesIdArray)){   //Creates a loop to loop through results
            echo "<option value=".$res['id'] .">" .$res['id']. "</option>";
            if($res['id'] == $moduleId && $isModuleOwnedBy == 0){
                $isModuleOwnedBy=1;
            }
        }
        echo "</select>";
        echo "<button type=\"submit\">Изпрати</button>";
        echo "</form>";

        $stmt = $con->prepare("SELECT * FROM  (SELECT * FROM moduleData where moduleId = ? ORDER BY id DESC LIMIT 20) sub LEFT JOIN modules m on m.userId = ? and m.id = ? ORDER BY sub.id ASC ;");
        $stmt->bind_param('iii',$moduleId, $id, $moduleId);
        $stmt->execute();
        $result = $stmt->get_result();
        if($showData){
            if($isModuleOwnedBy){
                if($moduleId[0] == '1'){
                    echo "<table class='table'>";
                    echo "<tr>";
                    echo "<th>Час и дата</th>";
                    echo "<th>КН на модул</th>";
                    echo "<th>Географска ширина</th>";
                    echo "<th>Географска дължина</th>";
                    echo "<th>Данни IMU</th>";
                    echo "</tr>";
                    while($list = mysqli_fetch_array($result)){   //Creates a loop to loop through results
                        echo "<tr>";
                        echo "<td>" . $list['eventTime'] . "</td><td>" . $list['moduleId'] . "</td><td>" . $list['latitude'] . "</td><td>" . $list['longitude'] . "</td><td>" . $list['imuEvent'] . "</td>";
                        echo "</tr>";
                    }
                }
                else{
                    echo "<table class='table'>";
                    echo "<tr>";
                    echo "<th>Час и дата</th>";
                    echo "<th>КН на модул</th>";
                    echo "<th>Шок сензор</th>";
                    echo "<th>RFID потвърждение</th>";
                    echo "</tr>";
                    while($list = mysqli_fetch_array($result)){   //Creates a loop to loop through results
                        echo "<tr>";
                        echo "<td>" . $list['eventTime'] . "</td><td>" . $list['moduleId'] . "</td><td>" . $list['shockEvent'] . "</td><td>" . $list['rfidEvent'] . "</td>";
                        echo "</tr>";
                    }
                }
            }else{
                echo ("Нямате достъп до тези ресурси");
                header("refresh:3;url=./events.php");
            }
        }

        ?>
</div>
</body>
</html>