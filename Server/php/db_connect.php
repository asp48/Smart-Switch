<?php
    function connect() {
        require_once __DIR__ . '/db_config.php';
 
        // Connecting to mysql database
    $con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE); 
 
        // returing connection cursor
        return $con;
    }
?>