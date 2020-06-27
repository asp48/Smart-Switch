<?php

require_once  'db_connect.php';
$con=connect();
 
if (mysqli_connect_errno($con))
{
   echo "400";
}
else{
    $uname  = $_POST["uname"];
    $pswd = $_POST["pswd"];
    
    
       $q1 = "Update UserData set Password ='".$pswd."' where Username = '".$uname."'";
       $res1 = mysqli_query($con,$q1);
       if($res1){
          echo "500";
       }
       else
          echo "450";
  
}
mysqli_close($con);
?>