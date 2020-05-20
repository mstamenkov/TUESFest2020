<?php
    session_start();
    $username;
    $password;
    $conf_password;
    if(!empty($_GET['apicall'])){
        $username = $_GET['username'];
        $password = $_GET['password'];
        $conf_password = $_GET['conf_password'];
	    $password = openssl_decrypt($password, 'AES-128-ECB', '2a925de8ca0248d7');
        $conf_password = openssl_decrypt($conf_password, 'AES-128-ECB', '2a925de8ca0248d7');
    }else{
        $username = filter_input(INPUT_POST, 'username');
        $password = filter_input(INPUT_POST, 'password');
        $conf_password = filter_input(INPUT_POST, 'conf_password');
        
    }
    if (!empty($username) and !empty($password) and !empty($conf_password)){
        require_once "./config.php";
        if (mysqli_connect_error()){
            die('Connect Error ('. mysqli_connect_errno() .') '
                . mysqli_connect_error());
        }
        else{
            if($password != $conf_password){
                echo ("entered passwords are not same");
                header("refresh:3;url=register.html");
                die();
            }
            $stmt = $con->prepare("SELECT * FROM users WHERE username = ?");
            $stmt->bind_param('s', $username);
            $stmt->execute();
            $result = $stmt->get_result();
            $user = $result->fetch_object();

            if(!is_null($user)){
                echo("username is already taken");
                header("refresh:3;url=register.html");
                die();
            }else if (!preg_match('/^[a-z1-9]+$/',$username)){
                echo("Special characters and capital letters not allowed");
                header("refresh:3;url=register.html");
                die();
            }

            $stmt = $con->prepare("INSERT INTO users(username,password) values(?,?)");
            $password_hash = password_hash($password,PASSWORD_DEFAULT);
            $stmt->bind_param("ss", $username,$password_hash);
                if ($stmt->execute()) {
		    if(empty($_GET['apicall'])){
			echo('OK');
                        header("Location: ./login.html");
		    }else{
			echo('OK');
		    }
                } else {
                    echo "Something went wrong. Please try again later.";
                }

            mysqli_close($con);
        }
    }
    else{
        echo "username or password should not be empty";
        header("refresh:3;url=register.html");
        die();
    }

?>

    
