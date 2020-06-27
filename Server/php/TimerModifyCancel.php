<?php

require_once  'db_connect.php';
$con=connect();
 
if (mysqli_connect_errno($con))
{
   echo "400";
}
else{
  
    /* $id = 1;
    $stime = 1000;
    $etime = 2000;
    $flag = 0; */

    $id = (int)$_POST["id"];
    $stime = (int)$_POST["stime"];
    $etime = (int)$_POST["etime"];
    $flag = (int)$_POST["flag"];

    
    if($flag == 1){
       $q1 = "Update Status set Stime=".$stime.", Etime=".$etime." where Id = ".$id."";
       $res1 = mysqli_query($con,$q1);
       if($res1){
          echo "500";
       }
       else
          echo "450";
  
    }
    else{
       $q2 = "delete from Status where Id = ".$id."";
       $res2 = mysqli_query($con,$q2);
       if($res2){
          echo "500";
       }
       else
          echo "450";
    
    }
  
}

mysqli_close($con);
?>