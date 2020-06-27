<?php

require_once  'db_connect.php';
$con=connect();
 
if (mysqli_connect_errno($con))
{
   echo "400";
}

else 
{
  $uname=$_POST["uname"];
   $pwd=$_POST["pswd"];

   $temp1 = "Insert into UserData (Username,Password) values ('$uname','$pwd')";
   $res1 = mysqli_query($con,$temp1);
   if($res1){
     
   $temp2 = "Insert into CheckPermission (Username,Switch1,Switch2,Switch3,Switch4) values ('$uname',0,0,0,0)";
   $res2 = mysqli_query($con,$temp2);
   if($res2){
      echo "500";
   }
   else{
      echo "480";
   }


   }
   else{
     echo "450";
   }

   

}

mysqli_close($con);
?>