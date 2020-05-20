<?php
    session_start();
    $username;
    $password;
    if(!empty($_GET['apicall'])){
        $username = $_GET['username'];
        $password = $_GET['password'];
	    $password = openssl_decrypt($password, 'AES-128-ECB', '2a925de8ca0248d7');
    }else{
        $username = filter_input(INPUT_POST, 'username');
        $password = filter_input(INPUT_POST, 'password');
    }
if ( !empty( $_POST ) || !empty($_GET['apicall']) ) {
    if ( !empty($username) && !empty($password) ) {
        // Getting submitted user data from database
        require_once "./config.php";
        $stmt = $con->prepare("SELECT * FROM users WHERE username = ?");
        $stmt->bind_param('s', $username);
        $stmt->execute();
        $result = $stmt->get_result();
        $user = $result->fetch_object();
        if(is_null($user)){
            echo("There is no user with this username");
            header("refresh:3;url=login.html");
            die();
        }

        // Verify user password and set $_SESSION
        if ( password_verify($password, $user->password ) ) {
            if(empty($_GET['apicall'])){
                    $_SESSION['user_id'] = $user->id;
                    $_SESSION['username'] = $username;
                    header("Location: ./index.php");
            }else{
            echo('OK');
            }
        }
        else{
            echo("Entered password does not match entered username");
            header("refresh:3;url=login.html");
        }

    }else {
        echo('Username and password fields must not be empty');
        header("refresh:3;url=login.html");
    }

}
    //mysqli_close($link);

?>
