<?php
session_start();
    if(empty($_GET['apicall'])){
        if (!isset($_SESSION['user_id'])) header('Location: ./login.html');
        require_once "./config.php";
        $stmt = $con->prepare("SELECT * FROM modules WHERE userId = ?");
        $stmt->bind_param('i', $_SESSION['user_id']);
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

        </head>

    <?php
        $submit = $_POST; //check if submit button is pressed
        if($submit){
            if(filter_input(INPUT_POST, 'type') == 'add'){
                $moduleId = filter_input(INPUT_POST, 'moduleid');
                $stmt = $con->prepare("SELECT * FROM modules WHERE id = ?");
                $stmt->bind_param('i', $moduleId);
                $stmt->execute();
                $check = $stmt->get_result();
                if(empty(mysqli_fetch_array($check))){ //check if this module ID is already registered
                    $stmt = $con->prepare("INSERT INTO modules(id,userId) values(?,?)");
                    $stmt->bind_param("ii",$moduleId,$_SESSION['user_id']);
                    if ($stmt->execute()) {
                        echo("OK");
                        header("Location: ./devices.php");
                    } else {
                        echo "Something went wrong. Please try again later.";
                    }
                }else{
                    echo ("This module is already registered");
                    header("refresh:3;url=./devices.php");
                }
            }else{
                $isModuleOwnedBy = 0; //variable which is used to determinate is this module is owned by that user
                $moduleId = filter_input(INPUT_POST, 'moduleid');
                while($row = mysqli_fetch_array($result)){
                    if($row['id'] == $moduleId){
                        $isModuleOwnedBy=1;
                        break;
                    }
                }
                if($isModuleOwnedBy){
                    $stmt = $con->prepare("SELECT * FROM modules WHERE id = ?");
                    $stmt->bind_param('i', $moduleId);
                    $stmt->execute();
                    $check = $stmt->get_result();
                    if(!empty(mysqli_fetch_array($check))){//check if this module ID is already deleted
                        $stmt = $con->prepare("DELETE FROM modules WHERE id = ?");
                        $stmt->bind_param("i",$moduleId);
                        if ($stmt->execute()) {
                            echo("OK");
                            header("Location: ./devices.php");
                        } else {
                            echo "Something went wrong. Please try again later.";
                        }
                    }else{
                        echo ("This module is already deleted");
                        header("refresh:3;url=./devices.php");
                    }
                }else {
                    echo ("This module is already deleted [2]");
                    header("refresh:3;url=./devices.php");
                }
            }


        }

    ?>
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
                <span><li><a href ="./devices.php" id="active">Модули</a></li></span>
                <span><li><a href ="./events.php">Данни</a></li></span>
                <span><li><a href ="./logout.php">Изход</a></li></span>
            </div>
        </ul>
    </div>
        <div class='block'>
            <form action='devices.php' method='POST'>
                <label for="moduleid">КН на модул</label>
                <input name="moduleid" value=""/>
                <label>Добавяне
                <input type="radio" name="type" value="add" checked="checked"></label>
                <label>Изтриване
                <input type="radio" name="type" value="delete"></label>
                <button type="submit">Изпълни</button>
        </form>
            <?php
            //echo "<div class='block'>";
            echo "<table class='table'>";
            echo "<tr>";
            echo "<th>КН на регистрирани модули</th>";
            echo "</tr>";
            while($row = mysqli_fetch_array($result)){   //Creates a loop to loop through results
                echo "<tr>";
                echo "<td>" . $row['id'] . "</td>";  //$row['index'] the index here is a field name
                echo "</tr>";
            }
            echo "</table>";
            ?>
        </div>
        </body>
        </html>
    <?php
    }else{

             require_once "./config.php";
             $stmt = $con->prepare("SELECT id FROM users WHERE username = ?");
             $stmt->bind_param("s", $_GET['username']);
             $stmt->execute();
             $result = $stmt->get_result();
             $row = mysqli_fetch_row($result);
         $begin = $_GET['page'];
         $end = $begin + 10;
             $stmt = $con->prepare("SELECT * FROM modules WHERE userId = ?");
             $stmt->bind_param('i', $row[0]);
             $stmt->execute();
             $result = $stmt->get_result();
             while($row = mysqli_fetch_array($result)) {
             $stmt = $con->prepare("SELECT * FROM moduleData WHERE moduleId = ?  LIMIT ?, ?");
             $stmt->bind_param('iii', $row[0], $begin, $end);
             $stmt->execute();
             $resultData = $stmt->get_result();
             while ($data = mysqli_fetch_array($resultData)) {
                 echo $data[2];
                 echo(';');
                 echo $data[1];
                 echo(';');
                 echo $data[3];
                 echo(';');
                 echo $data[4];
                 echo(';');
                 echo $data[5];
                 echo(';');
                 echo $data[6];
                 echo(';');
                 echo $data[7];
                 echo(';');

                 echo('/');
             }
         }
        }
    ?>
