<?php
// You'd put this code at the top of any "protected" page you create

// Always start this first
session_start();

if (isset( $_SESSION['user_id'] ) ) {
    echo ("IN");
    print_r($_SESSION);
    echo($_SESSION['user_id']);
} else {
    // Redirect them to the login page
    header("Location: http://localhost/diplomna_rabota/server/login.html");
}
?>